package io.sommers.aiintheipaw.commands.sprint;

import io.sommers.aiintheipaw.core.channel.ChannelService;
import io.sommers.aiintheipaw.core.channel.IChannel;
import io.sommers.aiintheipaw.core.event.IEventService;
import io.sommers.aiintheipaw.core.message.IReceivedMessage;
import io.sommers.aiintheipaw.core.sprint.ISprintRepository;
import io.sommers.aiintheipaw.core.sprint.Sprint;
import io.sommers.aiintheipaw.core.sprint.SprintStatus;
import io.sommers.aiintheipaw.core.util.ProviderId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SprintService {
    private final ChannelService channelService;
    private final IEventService eventService;
    private final ISprintRepository sprintRepository;
    private final SprintConfiguration sprintConfiguration;

    public SprintService(ChannelService channelService, IEventService eventService, ISprintRepository sprintRepository,
                         SprintConfiguration sprintConfiguration) {
        this.channelService = channelService;
        this.eventService = eventService;
        this.sprintRepository = sprintRepository;
        this.sprintConfiguration = sprintConfiguration;
    }

    public Mono<Void> setSprintToSignUp(IReceivedMessage message) {
        IChannel channel = message.getChannel();
        return this.createSprint(new Sprint(
                        this.sprintConfiguration.getSignUpDuration(),
                        this.sprintConfiguration.getInProgressDuration(),
                        channel.getId()
                ))
                .flatMap(sprint -> message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.sign_up")
                                        .withMessageArg(durationMessageBuilder -> durationMessageBuilder.withKey("duration")
                                                .withArg(Math.floor(this.sprintConfiguration.getSignUpDuration().getSeconds() / 60F))
                                                .withArg(this.sprintConfiguration.getSignUpDuration().getSeconds() % 60)
                                        )
                                        .withCommandArg("joinSprint")
                                        .withCommandArg("joinSprint <number>")
                                )
                                .then(scheduleSprintStatusUpdate(sprint, sprint.getStartTime().toInstant()))
                )
                .doOnError(Throwable::printStackTrace);

    }

    private Mono<Void> scheduleSprintStatusUpdate(Sprint sprint, Instant triggerTime) {
        try {
            return eventService.queueEvent(
                    "sprint",
                    triggerTime,
                    Map.of("sprintId", sprint.getId())
            ).then();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<String> handleSprintStatusUpdate(Sprint sprint) {
        return switch (sprint.getStatus()) {
            case SIGN_UP:
                yield this.setSprintToInProgress(sprint);
            case IN_PROGRESS:
                yield this.setSprintToWaitingCounts(sprint);
            case AWAITING_COUNTS:
                yield this.setSprintToCompleted(sprint);
            case COMPLETED:
                yield Mono.error(new IllegalStateException("Sprint is already completed"));
        };
    }

    public Mono<String> setSprintToInProgress(Sprint sprint) {
        return this.channelService.getChannel(new ProviderId(sprint.getChannelId()))
                .flatMap(channel -> channel.sendMessage(messageBuilder -> messageBuilder.withKey("sprint.in_progress")
                        .withMessageArg(durationMessageBuilder -> durationMessageBuilder.withKey("duration")
                                .withArg(Math.floor(this.sprintConfiguration.getSignUpDuration().getSeconds() / 60F))
                                .withArg(this.sprintConfiguration.getSignUpDuration().getSeconds() % 60)
                        )
                ))
                .flatMap(messageId -> {
                    sprint.setStatus(SprintStatus.IN_PROGRESS);
                    return this.sprintRepository.save(sprint)
                            .retry(3)
                            .thenReturn(messageId);
                })
                .flatMap(messageId -> this.scheduleSprintStatusUpdate(sprint, sprint.getEndTime().toInstant())
                        .then(Mono.just(messageId))
                );
    }

    public Mono<String> setSprintToWaitingCounts(Sprint sprint) {
        sprint.setStatus(SprintStatus.AWAITING_COUNTS);
        //return this.sprintRepository.save(sprint)
        //TODO Sprint Repository
        return Mono.justOrEmpty(sprint)
                .then(Mono.defer(() -> this.channelService.getChannel(new ProviderId(sprint.getChannelId()))))
                .flatMap(channel -> channel.sendMessage(messageBuilder -> messageBuilder.withKey("sprint.awaiting_counts")
                        .withCommandArg("submitSprint <number>")
                        .withMessageArg(durationMessageBuilder -> durationMessageBuilder.withKey("duration")
                                .withArg(Math.floor(this.sprintConfiguration.getAwaitingCountsDuration().getSeconds() / 60F))
                                .withArg(this.sprintConfiguration.getAwaitingCountsDuration().getSeconds() % 60)
                        )
                ))
                .flatMap(messageId -> this.scheduleSprintStatusUpdate(sprint, Instant.now().plus(this.sprintConfiguration.getAwaitingCountsDuration()))
                        .then(Mono.just(messageId))
                );
    }

    public Mono<String> setSprintToCompleted(Sprint sprint) {
        sprint.setStatus(SprintStatus.COMPLETED);
        return this.sprintRepository.save(sprint)
                .flatMap(savedSprint -> {
                    List<Pair<String, Long>> sprinters = new ArrayList<>();
                    //TODO Fix setSprintToCompleted
                    /*
                    for (String sprinter : savedSprint.getSprinters()) {
                        Long startingCount = savedSprint.getStartingCounts()
                                .get(sprinter);

                        Long endingCount = savedSprint.getEndingCounts()
                                .get(sprinter);

                        if (startingCount != null && endingCount != null) {
                            sprinters.add(Pair.of(sprinter, endingCount - startingCount));
                        } else {
                            sprinters.add(Pair.of(sprinter, 0L));
                        }
                    }

                    sprinters.sort(Comparator.comparingLong(Pair::getRight));
                    */
                    return this.channelService.getChannel(new ProviderId(savedSprint.getChannelId()))
                            .flatMap(channel -> channel.sendMessage(messageBuilder -> {
                                messageBuilder.withKey("sprint.completed");
                                AtomicInteger place = new AtomicInteger(1);
                                long seconds = ChronoUnit.SECONDS.between(
                                        savedSprint.getStartTime().toInstant(),
                                        savedSprint.getEndTime().toInstant()
                                );
                                double minutes = seconds / 60D;
                                for (Pair<String, Long> pair : sprinters) {
                                    messageBuilder.withAppendedMessage(appendedMessageBuilder -> appendedMessageBuilder.withKey("sprint.wpm")
                                            .withArg(place.getAndIncrement())
                                            .withUserArg(new ProviderId(pair.getFirst()))
                                            .withArg(pair.getSecond())
                                            .withArg(Math.floor(pair.getSecond() / minutes))
                                    );
                                }
                                return messageBuilder;
                            }));
                });
    }

    private Mono<Sprint> createSprint(Sprint sprint) {
        return this.sprintRepository.save(sprint);
    }

    public Mono<Sprint> getSprintById(UUID sprintId) {
        return this.sprintRepository.findById(sprintId);
    }

    public Mono<Sprint> findActiveSprint(IChannel channel) {
        return this.sprintRepository.findByChannelId(
                        channel.getId()
                                .asDocumentKey(),
                        PageRequest.ofSize(1)
                                .withSort(Sort.Direction.DESC, "created_date")
                )
                .next();
    }

    public Mono<Long> getLastWordCount(ProviderId userId) {
        return Mono.just(0L);
        //TODO fix getLastWordCount
        /*this.sprintRepository.findBySprintersContains(
                        userId.asDocumentKey(),
                        PageRequest.ofSize(5)
                                .withSort(Sort.Direction.DESC, "createdAt")
                )
                .flatMap(sprint -> Mono.justOrEmpty(sprint.getEndingCounts()
                        .get(userId.asDocumentKey())
                ))
                .reduce((a, b) -> a);
         */
    }

    public Mono<String> joinSprint(IReceivedMessage message, Mono<Long> wordCount) {
        return findActiveSprint(message.getChannel())
                .flatMap(sprint -> {
                    System.out.println(sprint.toString());
                    /*
                    final ProviderId id = message.getUser().getProviderId();
                    return wordCount.or(Mono.defer(() -> this.getLastWordCount(id)
                                    .or(Mono.just(0L)))
                            )
                            .flatMap(count -> {
                                sprint.addSprinter(id.asDocumentKey());
                                sprint.getStartingCounts().put(id.asDocumentKey(), count);
                                return this.sprintRepository.save(sprint)
                                        .flatMap(savedSpring -> message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.joined")
                                                .withArg(count)
                                        ));
                            })
                            .switchIfEmpty(message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.no_counts")));
                     */
                    //TODO joinSprint
                    return Mono.just("fuck");
                })
                .switchIfEmpty(message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.not_active")));
    }

    public Mono<String> submitWords(IReceivedMessage message, long wordCount) {
        return this.findActiveSprint(message.getChannel())
                .flatMap(sprint -> {
                    /*
                    String id = message.getUser().getProviderId().asDocumentKey();
                    if (sprint.getStatus() != SprintStatus.AWAITING_COUNTS) {
                        return message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.invalid_status"));
                    } else if (sprint.getSprinters().contains(id)) {
                        sprint.getEndingCounts().put(id, wordCount);
                        return this.sprintRepository.save(sprint)
                                .then(Mono.defer(() -> message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.counts_submitted"))));
                    } else {
                        return message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.user_not_sprinter"));
                    }

                     */
                    //TODO Fix submitWords
                    return Mono.just("");
                })
                .switchIfEmpty(message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.not_active")));
    }
}
