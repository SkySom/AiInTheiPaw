package io.sommers.ai.command.sprint;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class StartSprintJob implements Job {
    private final SprintService sprintService;

    public StartSprintJob(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail()
                .getJobDataMap();
        this.sprintService.startSprint(jobDataMap.getString("sprintId"))
                .block();
    }
}
