package io.sommers.aiintheipaw.eventhandler;

import java.util.Map;

public interface IEventHandler {
    String getName();

    Boolean handleEvent(Map<String, Object> detail);
}
