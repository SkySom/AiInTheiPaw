package io.sommers.aiintheipaw.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.domain.TwitchScopes;
import com.github.twitch4j.common.util.TypeConvert;
import com.github.twitch4j.eventsub.EventSubNotification;
import com.github.twitch4j.eventsub.EventSubTransport;
import com.github.twitch4j.eventsub.EventSubTransportMethod;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import com.github.twitch4j.eventsub.util.EventSubVerifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/twitch")
public class TwitchWebHookController {
    private final TwitchConfiguration twitchConfiguration;
    private final TwitchCommandHandler twitchCommandHandler;
    private final TwitchService twitchService;

    public TwitchWebHookController(TwitchConfiguration twitchConfiguration, TwitchCommandHandler twitchCommandHandler, TwitchService twitchService) {
        this.twitchConfiguration = twitchConfiguration;
        this.twitchCommandHandler = twitchCommandHandler;
        this.twitchService = twitchService;
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
        OAuth2Credential oAuth2Credential = this.twitchService.getIdentityProvider()
                .getCredentialByCode(code);

        CompletableFuture.supplyAsync(() -> this.twitchService.getTwitchClient()
                .getUsers(oAuth2Credential.getAccessToken(), null, null)
                .execute()
                .getUsers()
        ).thenCompose(users -> CompletableFuture.allOf(users.stream()
                .map(user -> CompletableFuture.supplyAsync(() -> this.twitchService.getTwitchClient()
                        .createEventSubSubscription(
                                null,
                                SubscriptionTypes.CHANNEL_CHAT_MESSAGE.prepareSubscription(
                                        builder -> builder.broadcasterUserId(user.getId())
                                                .build(),
                                        EventSubTransport.builder()
                                                .callback(this.twitchConfiguration.getEventSubSecret() + "/twitch/callback")
                                                .secret(this.twitchConfiguration.getEventSubSecret())
                                                .method(EventSubTransportMethod.WEBHOOK)
                                                .build()
                                )
                        )
                        .execute()
                ))
                .toArray(CompletableFuture[]::new)
        ));


        return ResponseEntity.ok(Mono.just("Successful Authentication"));
    }

    @GetMapping("/authentication/self")
    public Mono<Void> authSelf(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.FOUND);

        exchange.getResponse()
                .getHeaders()
                .setLocation(URI.create(this.twitchService.getIdentityProvider()
                        .getAuthenticationUrl(
                                List.of(TwitchScopes.CHAT_CHANNEL_BOT, TwitchScopes.CHAT_USER_BOT, TwitchScopes.HELIX_USER_CHAT_WRITE),
                                "Twitch" + UUID.randomUUID()
                        ))
                );

        return exchange.getResponse()
                .setComplete();
    }

    @GetMapping("/authentication/channel")
    public Mono<Void> authChannel(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.FOUND);

        exchange.getResponse()
                .getHeaders()
                .setLocation(URI.create(this.twitchService.getIdentityProvider()
                        .getAuthenticationUrl(
                                List.of(TwitchScopes.CHAT_CHANNEL_BOT),
                                "Twitch" + UUID.randomUUID()
                        ))
                );

        return exchange.getResponse()
                .setComplete();
    }
}
