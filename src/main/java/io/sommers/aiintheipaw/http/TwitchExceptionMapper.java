package io.sommers.aiintheipaw.http;

import io.sommers.aiintheipaw.model.service.IService;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import java.util.Map;

public class TwitchExceptionMapper implements ResponseExceptionMapper<ServiceCallException> {
    @Inject
    IService twitch;

    @Override
    public ServiceCallException toThrowable(Response response) {
        return new ServiceCallException(twitch, response.readEntity(Map.class));
    }
}
