package io.sommers.ai.service;

import io.sommers.ai.configuration.SprintConfiguration;
import io.sommers.ai.job.UpdateSprintStatusJob;
import io.sommers.ai.model.Message;
import io.sommers.ai.model.channel.IChannel;
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

    public Mono<Void> setSprintToSignUp(IChannel channel) {
        return this.createSprint(new Sprint(
                        this.sprintConfiguration.getSignUpDuration(),
                        this.sprintConfiguration.getInProgressionDuration(),
                        channel.getId()
                ))
                .flatMap(sprint -> channel.sendMessage(messageBuilder -> messageBuilder.withKey("sprint.sign_up")
                                        .withArg(this.sprintConfiguration.getSignUpDuration().toString())
                                        .withCommandArg("joinSprint")
                                )
                                .then(scheduleSprintStatusUpdate(sprint, SprintStatus.IN_PROGRESS, sprint.getStartTime().toDate()))
                )
                .doOnError(Throwable::printStackTrace);

    }

    private Mono<Void> scheduleSprintStatusUpdate(Sprint sprint, SprintStatus nextStatus, Date triggerTime) {
        try {
            scheduler.scheduleJob(
                    JobBuilder.newJob(UpdateSprintStatusJob.class)
                            .usingJobData("sprintId", sprint.getId())
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
                    .flatMap(channel -> channel.sendMessage(new Message("The Sprint starts now and runs for 20 minutes")))
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
                    .flatMap(channel -> channel.sendMessage(new Message("Time's up! Please give your final word count with !words. You have 3 minutes")))
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
                    .flatMap(channel -> channel.sendMessage(new Message("Time's up! Please give your final word count with !words. You have 3 minutes")))
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
}
