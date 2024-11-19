package io.sommers.ai.command.sprint;

import io.sommers.ai.model.IChannel;
import io.sommers.ai.model.Message;
import org.quartz.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;

@Service
public class SprintService {
    private final Scheduler scheduler;

    public SprintService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Mono<Void> scheduleSprint(IChannel channel) {
        return channel.replyTo(new Message("A Sprint will Start in 60 Seconds. Type !sameSprint to use last word count, " +
                        "or !wordSprint <words> to use a new word count"
                ))
                .then(scheduleSprintStart(channel));

    }

    private Mono<Void> scheduleSprintStart(IChannel channel) {
        try {
            scheduler.scheduleJob(
                    JobBuilder.newJob(StartSprintJob.class)
                            .usingJobData("service", channel.getService())
                            .usingJobData("channelId", channel.getId())
                            .build(),
                    TriggerBuilder.newTrigger()
                            .startAt(Date.from(Instant.now()
                                    .plusSeconds(10)
                            ))
                            .build()
            );
            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<String> startSprint(IChannel channel) {
        return channel.replyTo(new Message("The Sprint starts now and runs for 20 minutes"));
    }
}
