package io.sommers.aiintheipaw.routing;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.request.SendMessageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@ApplicationScoped
@Path("bot")
public class MessageRoute {
    @POST
    @Path("message")
    public Uni<Void> sendMessage(@Valid SendMessageRequest sendMessageRequest) {
        return Uni.createFrom()
                .voidItem();
    }
}
