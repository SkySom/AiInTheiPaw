package io.sommers.ai.command.sprint;

import io.sommers.ai.model.Message;
import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.service.ChannelService;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class SprintService {
    private final ChannelService channelService;
    private final Scheduler scheduler;
    private final ISprintRepository sprintRepository;

    public SprintService(ChannelService channelService, Scheduler scheduler, ISprintRepository sprintRepository) {
        this.channelService = channelService;
        this.scheduler = scheduler;
        this.sprintRepository = sprintRepository;
    }

    public Mono<Void> scheduleSprint(IChannel channel) {
        return this.createSprint(new Sprint(Duration.of(1, ChronoUnit.MINUTES), channel.getId()))
                .flatMap(sprint -> channel.sendMessage(new Message(
                                        "A Sprint will Start in 1 minute. " +
                                                "Type !sameSprint to use last word count, " +
                                                "or !wordSprint <words> to use a new word count"
                                ))
                                .then(scheduleSprintStart(sprint))
                )
                .doOnError(Throwable::printStackTrace);

    }

    private Mono<Void> scheduleSprintStart(Sprint sprint) {
        try {
            scheduler.scheduleJob(
                    JobBuilder.newJob(StartSprintJob.class)
                            .usingJobData("sprintId", sprint.getId())
                            .build(),
                    TriggerBuilder.newTrigger()
                            .startAt(sprint.getStartTime()
                                    .toDate()
                            )
                            .build()
            );
            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<String> startSprint(String sprintId) {
        return this.getSprintById(sprintId)
                .flatMap(sprint -> this.channelService.getChannel(sprint.getChannelId())
                        .flatMap(channel -> channel.sendMessage(new Message("The Sprint starts now and runs for 20 minutes")))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("???");
                    return Mono.just("fuck");
                }));
    }

    private Mono<Sprint> createSprint(Sprint sprint) {
        return this.sprintRepository.save(sprint);
    }

    private Mono<Sprint> getSprintById(String sprintId) {
        return this.sprintRepository.findById(sprintId);
    }
}
