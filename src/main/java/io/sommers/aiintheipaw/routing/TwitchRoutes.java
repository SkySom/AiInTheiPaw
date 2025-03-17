package io.sommers.aiintheipaw.routing;

import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Header;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.util.TwitchEventSubVerifier;
import io.sommers.aiintheipaw.validation.MaxDuration;
import io.sommers.aiintheipaw.validation.TwitchMessageId;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.http.HttpServerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;

@ApplicationScoped
@RouteBase(path = "twitch")
public class TwitchRoutes {

    @ConfigProperty(name = "twitch.event.sub.secret")
    String twitchEventSubSecret;

    @Route(path = "callback")
    public Uni<Void> handleWebhook(
            @Body String body,
            @Body JsonObject jsonBody,
            HttpServerResponse httpServerResponse,
            @Header("Twitch-Eventsub-Message-Id") @Valid @TwitchMessageId String messageId,
            @Header("Twitch-Eventsub-Message-Type") @Valid @NotBlank String messageType,
            @Header("Twitch-Eventsub-Message-Timestamp") @Valid @MaxDuration(maxMillis = 600000) String messageTimestamp,
            @Header("Twitch-Eventsub-Message-Signature") @Valid @NotBlank String messageSignature
    ) {
        boolean validSignature = TwitchEventSubVerifier.verifySignature(
                twitchEventSubSecret,
                messageId,
                messageTimestamp,
                body.getBytes(StandardCharsets.UTF_8),
                messageSignature
        );
        if (validSignature) {
            return switch (messageType) {
                case "webhook_callback_verification" -> handleVerification(jsonBody, httpServerResponse);
                case "notification" -> handleNotification(jsonBody, httpServerResponse);
                default -> httpServerResponse.setStatusCode(204)
                        .end();
            };
        } else {
            return httpServerResponse.setStatusCode(403)
                    .end();
        }


    }

    private Uni<Void> handleNotification(JsonObject message, HttpServerResponse httpServerResponse) {
        System.out.println(message.toString());
        return httpServerResponse.setStatusCode(204)
                .end();
    }

    private Uni<Void> handleVerification(JsonObject body, HttpServerResponse httpServerResponse) {
        String challenge = body.getString("challenge");
        if (challenge == null) {
            return httpServerResponse.setStatusCode(400)
                    .end();

        } else {
            return httpServerResponse.setStatusCode(200)
                    .putHeader("Content-Type", "text/plain")
                    .putHeader("Content-Length", Integer.toString(challenge.length()))
                    .send(challenge);
        }
    }
}
