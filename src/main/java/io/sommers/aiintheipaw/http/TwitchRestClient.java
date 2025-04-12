package io.sommers.aiintheipaw.http;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.twitch.TwitchDataResponse;
import io.sommers.aiintheipaw.model.twitch.message.SendTwitchMessageRequest;
import io.sommers.aiintheipaw.model.twitch.message.SendTwitchMessageResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("helix")
@RegisterRestClient(configKey = "twitch-api")
@RegisterProvider(TwitchExceptionMapper.class)
public interface TwitchRestClient {
    @POST
    @Path("chat/messages")
    @ClientHeaderParam(name = "Client-Id", value = "${twitch.client-id}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer ${twitch.client-secret}")
    Uni<TwitchDataResponse<SendTwitchMessageResponse>> sendMessage(@Valid SendTwitchMessageRequest sendMessageRequest);
}
