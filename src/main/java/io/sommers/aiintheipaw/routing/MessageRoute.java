package io.sommers.aiintheipaw.routing;

import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.request.SendMessageRequest;
import jakarta.validation.Valid;

@RouteBase(path = "bot")
public class MessageRoute {
    @Route(path = "message", methods = HttpMethod.POST)
    public Uni<Void> sendMessage(@Body @Valid SendMessageRequest sendMessageRequest) {
        return Uni.createFrom()
                .voidItem();
    }
}
