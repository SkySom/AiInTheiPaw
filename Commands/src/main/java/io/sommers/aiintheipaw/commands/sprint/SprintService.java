package io.sommers.aiintheipaw.commands.sprint;

import io.sommers.aiintheipaw.core.channel.ChannelService;
import io.sommers.aiintheipaw.core.channel.IChannel;
import io.sommers.aiintheipaw.core.message.IReceivedMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SprintService {
    private final ChannelService channelService;
    private final Scheduler scheduler;
    private final ISprintRepository sprintRepository;
    private final SprintConfiguration sprintConfiguration;

    public SprintService(ChannelService channelService, Scheduler scheduler, ISprintRepository sprintRepository,
                         SprintConfiguration sprintConfiguration) {
        this.channelService = channelService;
        this.scheduler = scheduler;
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
                                .then(scheduleSprintStatusUpdate(sprint, sprint.getStartTime().toDate()))
                )
                .doOnError(Throwable::printStackTrace);

    }

    private Mono<Void> scheduleSprintStatusUpdate(Sprint sprint, Date triggerTime) {
        try {
            scheduler.scheduleJob(
                    JobBuilder.newJob(UpdateSprintStatusJob.class)
                            .usingJobData("sprintId", sprint.getDocumentId())
                            .build(),
                    TriggerBuilder.newTrigger()
                            .startAt(triggerTime)
                            .build()
            );
            return Mono.empty();
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
                .flatMap(messageId -> this.scheduleSprintStatusUpdate(sprint, sprint.getEndTime().toDate())
                        .then(Mono.just(messageId))
                );
    }

    public Mono<String> setSprintToWaitingCounts(Sprint sprint) {
        sprint.setStatus(SprintStatus.AWAITING_COUNTS);
        return this.sprintRepository.save(sprint)
                .then(Mono.defer(() -> this.channelService.getChannel(new ProviderId(sprint.getChannelId()))))
                .flatMap(channel -> channel.sendMessage(messageBuilder -> messageBuilder.withKey("sprint.awaiting_counts")
                        .withCommandArg("submitSprint <number>")
                        .withMessageArg(durationMessageBuilder -> durationMessageBuilder.withKey("duration")
                                .withArg(Math.floor(this.sprintConfiguration.getAwaitingCountsDuration().getSeconds() / 60F))
                                .withArg(this.sprintConfiguration.getAwaitingCountsDuration().getSeconds() % 60)
                        )
                ))
                .flatMap(messageId -> this.scheduleSprintStatusUpdate(sprint, new Date(Instant.now().plus(this.sprintConfiguration.getAwaitingCountsDuration()).toEpochMilli()))
                        .then(Mono.just(messageId))
                );
    }

    public Mono<String> setSprintToCompleted(Sprint sprint) {
        sprint.setStatus(SprintStatus.COMPLETED);
        return this.sprintRepository.save(sprint)
                .flatMap(savedSprint -> {
                    List<Pair<String, Long>> sprinters = new ArrayList<>();
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
                    return this.channelService.getChannel(new ProviderId(savedSprint.getChannelId()))
                            .flatMap(channel -> channel.sendMessage(messageBuilder -> {
                                messageBuilder.withKey("sprint.completed");
                                AtomicInteger place = new AtomicInteger(1);
                                long seconds = savedSprint.getEndTime().getSeconds() - savedSprint.getStartTime().getSeconds();
                                double minutes = seconds / 60D;
                                for (Pair<String, Long> pair : sprinters) {
                                    messageBuilder.withAppendedMessage(appendedMessageBuilder -> appendedMessageBuilder.withKey("sprint.wpm")
                                            .withArg(place.getAndIncrement())
                                            .withUserArg(new ProviderId(pair.getLeft()))
                                            .withArg(pair.getRight())
                                            .withArg(Math.floor(pair.getRight() / minutes))
                                    );
                                }
                                return messageBuilder;
                            }));
                });
    }

    private Mono<Sprint> createSprint(Sprint sprint) {
        return this.sprintRepository.save(sprint);
    }

    public Mono<Sprint> getSprintById(String sprintId) {
        return this.sprintRepository.findById(sprintId);
    }

    public Mono<Sprint> findActiveSprint(IChannel channel) {
        return this.sprintRepository.findByChannelId(
                        channel.getId()
                                .asDocumentKey(),
                        PageRequest.ofSize(1)
                                .withSort(Sort.Direction.DESC, "createdAt")
                )
                .next();
    }

    public Mono<Long> getLastWordCount(ProviderId userId) {
        return this.sprintRepository.findBySprintersContains(
                        userId.asDocumentKey(),
                        PageRequest.ofSize(5)
                                .withSort(Sort.Direction.DESC, "createdAt")
                )
                .flatMap(sprint -> Mono.justOrEmpty(sprint.getEndingCounts()
                        .get(userId.asDocumentKey())
                ))
                .reduce((a, b) -> a);
    }

    public Mono<String> joinSprint(IReceivedMessage message, Mono<Long> wordCount) {
        return findActiveSprint(message.getChannel())
                .flatMap(sprint -> {
                    System.out.println(sprint.toString());
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
                })
                .switchIfEmpty(message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.not_active")));
    }

    public Mono<String> submitWords(IReceivedMessage message, long wordCount) {
        return this.findActiveSprint(message.getChannel())
                .flatMap(sprint -> {
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
                })
                .switchIfEmpty(message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.not_active")));
    }
}
