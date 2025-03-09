package io.sommers.aiintheipaw.core.event;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

public interface IEventService {
    Mono<String> queueEvent(String target, Instant triggerTime, Map<String, Object> data);
}
