package io.sommers.aiintheipaw.awscore;

import io.sommers.aiintheipaw.core.event.IEventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.scheduler.SchedulerClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class AWSEventService implements IEventService {

    private final EventBridgeAsyncClient eventBridgeClient;
    private final SchedulerClient schedulerClient;

    public AWSEventService(AwsCredentialsProvider credentialsProvider, AwsRegionProvider regionProvider) {
        this.eventBridgeClient = EventBridgeAsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(regionProvider.getRegion())
                .build();
        this.schedulerClient = SchedulerClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(regionProvider.getRegion())
                .build();
    }

    @Override
    public Mono<String> queueEvent(String target, Map<String, Object> data) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> ""
                //this.schedulerClient.createSchedule(builder -> builder.actionAfterCompletion(ActionAfterCompletion.DELETE)
                //        .scheduleExpression()
                //)
        ));
    }
}
