package io.sommers.aiintheipaw.http;

import io.sommers.aiintheipaw.model.service.IService;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import java.util.Collections;
import java.util.Map;

public class TwitchExceptionMapper implements ResponseExceptionMapper<ServiceCallException> {
    @SuppressWarnings("unchecked")
    private static final GenericType<Map<String, Object>> TYPE = (GenericType<Map<String, Object>>) GenericType.forInstance(Collections.<String, Object>emptyMap());

    @Inject
    IService twitch;

    @Override
    public ServiceCallException toThrowable(Response response) {
        return new ServiceCallException(twitch, response.readEntity(TYPE));
    }
}
