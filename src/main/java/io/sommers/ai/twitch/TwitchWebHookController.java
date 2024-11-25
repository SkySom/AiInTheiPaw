package io.sommers.ai.twitch;

import com.github.twitch4j.common.util.TypeConvert;
import com.github.twitch4j.eventsub.EventSubNotification;
import com.github.twitch4j.eventsub.util.EventSubVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/twitch")
public class TwitchWebHookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchWebHookController.class);

    private final TwitchConfiguration twitchConfiguration;
    private final TwitchCommandHandler twitchCommandHandler;

    public TwitchWebHookController(TwitchConfiguration twitchConfiguration, TwitchCommandHandler twitchCommandHandler) {
        this.twitchConfiguration = twitchConfiguration;
        this.twitchCommandHandler = twitchCommandHandler;
    }

    @PostMapping("/callback")
    public ResponseEntity<Mono<String>> receiveCallback(
            @RequestHeader(name = "Twitch-Eventsub-Message-Id") String messageId,
            @RequestHeader(name = "Twitch-Eventsub-Message-Timestamp") String timestamp,
            @RequestHeader(name = "Twitch-Eventsub-Message-Signature") String signature,
            @RequestBody String eventSubNotification
    ) {
        boolean validId = EventSubVerifier.verifyMessageId(messageId);
        boolean validTimestamp = EventSubVerifier.verifyTimestamp(timestamp);
        boolean validSecret = EventSubVerifier.verifySignature(
                this.twitchConfiguration.getEventSubSecret(),
                messageId,
                timestamp,
                eventSubNotification.getBytes(StandardCharsets.UTF_8),
                signature
        );

        if (validId && validTimestamp && validSecret) {
            EventSubNotification notification = TypeConvert.jsonToObject(eventSubNotification, EventSubNotification.class);

            if (notification != null) {
                if (notification.getChallenge() != null) {
                    return ResponseEntity.ok(Mono.just(notification.getChallenge()));
                } else {
                    LOGGER.info("Received EventSubNotification: {}", notification);
                    return ResponseEntity.ok(this.twitchCommandHandler.tryExecuteCommand(notification)
                            .thenReturn("{}")
                    );
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(Mono.empty());
            }
        } else {
            return ResponseEntity.status(403)
                    .body(Mono.empty());
        }
    }

    @GetMapping("/oauth")
    public ResponseEntity<Mono<String>> getOauthToken(
            @RequestParam String code
    ) {
        return ResponseEntity.ok(Mono.just("Successful Authentication"));
    }

    @GetMapping("/authentication/self")
    public Mono<Void> authSelf(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.FOUND);

        exchange.getResponse()
                .getHeaders()
                .setLocation(getOAuthUri("channel:bot", "user:bot", "user:write:chat"));

        return exchange.getResponse()
                .setComplete();
    }

    @GetMapping("/authentication/channel")
    public Mono<Void> authChannel(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.FOUND);

        exchange.getResponse()
                .getHeaders()
                .setLocation(getOAuthUri("channel:bot"));

        return exchange.getResponse()
                .setComplete();
    }

    public URI getOAuthUri(String... scopes) {
        return URI.create(
                "https://id.twitch.tv/oauth2/authorize" +
                        "?response_type=code&client_id=" + this.twitchConfiguration.getClientId() +
                        "&redirect_uri=" + this.twitchConfiguration.getEventCallbackDomain() + "/twitch/oauth" +
                        "&scope=" + Arrays.stream(scopes).map(scope -> URLEncoder.encode(scope, StandardCharsets.UTF_8)).collect(Collectors.joining("%20")) +
                        "&state=" + UUID.randomUUID()
        );
    }
}
