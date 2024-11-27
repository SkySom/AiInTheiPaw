package io.sommers.ai.service;

import io.sommers.ai.configuration.SprintConfiguration;
import io.sommers.ai.job.UpdateSprintStatusJob;
import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.message.IReceivedMessage;
import io.sommers.ai.model.sprint.Sprint;
import io.sommers.ai.model.sprint.SprintStatus;
import io.sommers.ai.repository.ISprintRepository;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.OptionalLong;

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
                                .then(scheduleSprintStatusUpdate(sprint, SprintStatus.IN_PROGRESS, sprint.getStartTime().toDate()))
                )
                .doOnError(Throwable::printStackTrace);

    }

    private Mono<Void> scheduleSprintStatusUpdate(Sprint sprint, SprintStatus nextStatus, Date triggerTime) {
        try {
            scheduler.scheduleJob(
                    JobBuilder.newJob(UpdateSprintStatusJob.class)
                            .usingJobData("sprintId", sprint.getDocumentId())
                            .usingJobData("nextSprintStatus", nextStatus.name())
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

    public Mono<String> handleSprintStatusUpdate(Sprint sprint, SprintStatus nextStatus) {
        return switch (nextStatus) {
            case IN_PROGRESS:
                yield this.setSprintToInProgress(sprint);
            case AWAITING_COUNTS:
                yield this.setSprintToWaitingCounts(sprint);
            case COMPLETED:
                yield this.setSprintToCompleted(sprint);
            case SIGN_UP:
                yield Mono.error(new IllegalStateException("Cannot go back to Sign up State"));
        };
    }

    public Mono<String> setSprintToInProgress(Sprint sprint) {
        if (sprint.getStatus() == SprintStatus.SIGN_UP) {
            return this.channelService.getChannel(sprint.getChannelId())
                    .flatMap(channel -> channel.sendMessage(messageBuilder -> messageBuilder.withKey("sprint.in_progress")
                            .withMessageArg(durationMessageBuilder -> durationMessageBuilder.withKey("duration")
                                    .withArg(Math.floor(this.sprintConfiguration.getSignUpDuration().getSeconds() / 60F))
                                    .withArg(this.sprintConfiguration.getSignUpDuration().getSeconds() % 60)
                            )
                    ))
                    .flatMap(messageId -> this.scheduleSprintStatusUpdate(sprint, SprintStatus.AWAITING_COUNTS, sprint.getEndTime().toDate())
                            .then(Mono.just(messageId))
                    );
        } else {
            return Mono.error(new IllegalArgumentException("Sprint must be in Sign up Status to switch to begin"));
        }
    }

    public Mono<String> setSprintToWaitingCounts(Sprint sprint) {
        if (sprint.getStatus() == SprintStatus.IN_PROGRESS) {
            return this.channelService.getChannel(sprint.getChannelId())
                    .flatMap(channel -> channel.sendMessage(messageBuilder -> messageBuilder.withMessage("Time's up! Please give your final word count with !words. You have 3 minutes")))
                    .flatMap(messageId -> this.scheduleSprintStatusUpdate(sprint, SprintStatus.COMPLETED, Date.from(Instant.now().plus(3, ChronoUnit.MINUTES)))
                            .then(Mono.just(messageId))
                    );
        } else {
            return Mono.error(new IllegalArgumentException("Sprint must be in Sign up Status to switch to begin"));
        }
    }

    public Mono<String> setSprintToCompleted(Sprint sprint) {
        if (sprint.getStatus() == SprintStatus.AWAITING_COUNTS) {
            return this.channelService.getChannel(sprint.getChannelId())
                    .flatMap(channel -> channel.sendMessage(messageBuilder -> messageBuilder.withMessage("Time's up! Please give your final word count with !words. You have 3 minutes")))
                    .flatMap(messageId -> this.scheduleSprintStatusUpdate(sprint, SprintStatus.COMPLETED, Date.from(Instant.now().plus(3, ChronoUnit.MINUTES)))
                            .then(Mono.just(messageId))
                    );
        } else {
            return Mono.error(new IllegalArgumentException("Sprint must be in Sign up Status to switch to begin"));
        }
    }

    private Mono<Sprint> createSprint(Sprint sprint) {
        return this.sprintRepository.save(sprint);
    }

    public Mono<Sprint> getSprintById(String sprintId) {
        return this.sprintRepository.findById(sprintId);
    }

    public Mono<Sprint> findActiveSprint(IChannel channel) {
        ProviderId id = channel.getId();
        return this.sprintRepository.findByStatusInOrderByLastUpdated(
                        List.of(SprintStatus.SIGN_UP, SprintStatus.IN_PROGRESS, SprintStatus.AWAITING_COUNTS)
                )
                .filter(sprint -> sprint.getStatus() != SprintStatus.COMPLETED)
                .next();
    }

    public Mono<String> joinSprint(IReceivedMessage message, OptionalLong wordCount) {
        return findActiveSprint(message.getChannel())
                .flatMap(sprint -> {
                    sprint.getStartingCounts().put(message.getUser().getProviderId().id(), wordCount.orElse(0L));
                    return this.sprintRepository.save(sprint);
                })
                .flatMap(sprint -> message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.joined")
                        .withArg(wordCount.orElse(0L))
                ))
                .switchIfEmpty(message.replyTo(messageBuilder -> messageBuilder.withKey("sprint.not_active"))
                        .then(Mono.empty())
                );
    }
}
