package io.sommers.aiintheipaw.eventhandler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

public class LambdaEventHandler implements RequestHandler<ScheduledEvent, Boolean> {


    @Override
    public Boolean handleRequest(ScheduledEvent scheduledEvent, Context context) {
        return null;
    }
}
