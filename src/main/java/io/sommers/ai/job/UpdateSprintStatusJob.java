package io.sommers.ai.job;

import io.sommers.ai.model.sprint.SprintStatus;
import io.sommers.ai.service.SprintService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class UpdateSprintStatusJob implements Job {
    private final SprintService sprintService;

    public UpdateSprintStatusJob(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail()
                .getJobDataMap();

        SprintStatus.fromString(jobDataMap.getString("nextSprintStatus"))
                .flatMap(sprintStatus -> this.sprintService.getSprintById(jobDataMap.getString("sprintId"))
                        .flatMap(sprint -> this.sprintService.handleSprintStatusUpdate(sprint, sprintStatus))
                )
                .block();
    }
}
