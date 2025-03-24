package io.sommers.aiintheipaw.routing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.logic.ChannelLogic;
import io.sommers.aiintheipaw.logic.UserSourceLogic;
import io.sommers.aiintheipaw.model.channel.IChannel;
import io.sommers.aiintheipaw.model.user.IUser;
import io.sommers.aiintheipaw.util.TwitchEventSubVerifier;
import io.sommers.aiintheipaw.validation.MaxDuration;
import io.sommers.aiintheipaw.validation.TwitchMessageId;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;

@ApplicationScoped
@Path("twitch")
public class TwitchRoutes {

    @Inject
    ChannelLogic channelLogic;
    @Inject
    UserSourceLogic userSourceLogic;
    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "twitch.event.sub.secret")
    String twitchEventSubSecret;

    @POST
    @Path("callback")
    public Uni<Response> handleWebhook(
            String body,
            @HeaderParam("Twitch-Eventsub-Message-Id") @Valid @TwitchMessageId String messageId,
            @HeaderParam("Twitch-Eventsub-Message-Type") @Valid @NotBlank String messageType,
            @HeaderParam("Twitch-Eventsub-Message-Timestamp") @Valid @MaxDuration(maxMillis = 600000) String messageTimestamp,
            @HeaderParam("Twitch-Eventsub-Message-Signature") @Valid @NotBlank String messageSignature
    ) throws JsonProcessingException {
        boolean validSignature = TwitchEventSubVerifier.verifySignature(
                twitchEventSubSecret,
                messageId,
                messageTimestamp,
                body.getBytes(StandardCharsets.UTF_8),
                messageSignature
        );
        JsonObject jsonBody = objectMapper.readValue(body, JsonObject.class);
        if (validSignature) {
            return switch (messageType) {
                case "webhook_callback_verification" -> handleVerification(jsonBody);
                case "notification" -> handleNotification(jsonBody)
                        .map(ignored -> Response.noContent()
                                .build()
                        );
                case "revocation" -> handleRevocation(jsonBody);
                default -> Uni.createFrom()
                        .nullItem();
            };
        } else {
            return Uni.createFrom()
                    .nullItem();
        }
    }

    private Uni<Response> handleRevocation(JsonObject jsonBody) {
        System.out.println("Revocation for: " + Option.of(jsonBody.getJsonObject("subscription"))
                .map(jsonObject -> jsonObject.getString("status"))
                .getOrElse("Unknown")
        );
        return Uni.createFrom()
                .item(Response.noContent()
                        .build()
                );
    }

    private Uni<Void> handleNotification(JsonObject message) {
        JsonObject eventJson = message.getJsonObject("event");
        if (eventJson != null) {
            String channelId = eventJson.getString("broadcaster_user_id");
            String userId = eventJson.getString("chatter_user_id");

            Uni<IChannel> channelUni = channelLogic.findByServiceGuildIdAndChannelId(
                    "twitch",
                    null,
                    channelId
            );

            Uni<IUser> userUni = userSourceLogic.findByServiceAndId("twitch", userId);

            return Uni.combine()
                    .all()
                    .unis(channelUni, userUni)
                    .asTuple()
                    .flatMap(channelUser -> {
                        System.out.println("User: " + userId + " Channel: " + channelId);
                        return Uni.createFrom()
                                .voidItem();
                    })
                    .onFailure()
                    .recoverWithUni(Uni.createFrom()
                            .voidItem()
                    );
        }

        return Uni.createFrom()
                .nothing();
    }

    private Uni<Response> handleVerification(JsonObject body) {
        String challenge = body.getString("challenge");
        if (challenge == null) {
            return Uni.createFrom()
                    .item(Response.status(Status.BAD_REQUEST)
                            .build()
                    );
        } else {
            return Uni.createFrom()
                    .item(Response.ok(challenge)
                            .build()
                    );
        }
    }
}
