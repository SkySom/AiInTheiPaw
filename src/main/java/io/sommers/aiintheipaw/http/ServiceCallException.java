package io.sommers.aiintheipaw.http;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.sommers.aiintheipaw.model.service.IService;
import org.jboss.resteasy.reactive.RestResponse.Status;

import java.util.Map;

public class ServiceCallException extends HttpProblem {
    public ServiceCallException(IService service, Map<String, Object> response) {
        super(HttpProblem.builder()
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .withDetail("Failed to call service " + service.getName())
                .with("Service Error", response)
        );
    }
}
