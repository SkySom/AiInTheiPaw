package io.sommers.ai.command.sprint;

import io.sommers.ai.manager.ChannelManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class StartSprintJob implements Job {
    private final SprintService sprintService;
    private final ChannelManager channelManager;

    public StartSprintJob(SprintService sprintService, ChannelManager channelManager) {
        this.sprintService = sprintService;
        this.channelManager = channelManager;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail()
                .getJobDataMap();
        this.channelManager.getChannel(jobDataMap.getString("service"), jobDataMap.getString("channelId"))
                .flatMap(this.sprintService::startSprint)
                .block();
    }
}
