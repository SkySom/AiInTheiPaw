package io.sommers.ai.job;

import io.sommers.ai.service.SprintService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import reactor.core.publisher.Mono;

public class UpdateSprintStatusJob implements Job {
    private final SprintService sprintService;

    public UpdateSprintStatusJob(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail()
                .getJobDataMap();

        this.sprintService.getSprintById(jobDataMap.getString("sprintId"))
                .flatMap(this.sprintService::handleSprintStatusUpdate)
                .switchIfEmpty(Mono.error(new IllegalStateException("No sprint found")))
                .log()
                .block();
    }
}
