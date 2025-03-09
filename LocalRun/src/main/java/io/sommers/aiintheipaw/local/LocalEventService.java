package io.sommers.aiintheipaw.local;

import io.sommers.aiintheipaw.core.event.IEventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Component
public class LocalEventService implements IEventService {

    @Override
    public Mono<String> queueEvent(String target, Instant triggerTime, Map<String, Object> data) {
        return Mono.just("Nope");
    }
}
