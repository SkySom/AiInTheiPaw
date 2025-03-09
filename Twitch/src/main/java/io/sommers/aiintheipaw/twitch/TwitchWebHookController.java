package io.sommers.aiintheipaw.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.domain.TwitchScopes;
import com.github.twitch4j.common.util.TypeConvert;
import com.github.twitch4j.eventsub.*;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import com.github.twitch4j.eventsub.util.EventSubVerifier;
import com.github.twitch4j.helix.domain.EventSubSubscriptionList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.RxReactiveStreams;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<Mono<String>> authenticated(
            @RequestParam String code,
            @RequestParam String state
    ) {
        OAuth2Credential oAuth2Credential = this.twitchService.getIdentityProvider()
                .getCredentialByCode(code);

        boolean add = true;
        String[] statePieces = state.split("_");
        if (statePieces.length >= 3) {
            if (statePieces[1].equals("RemoveBot")) {
                add = false;
            }
        }

        if (add) {
            return ResponseEntity.ok(this.getUsers(oAuth2Credential)
                    .flatMap(this::createEventSubSubscription)
                    .then(Mono.just("Successfully added bot"))
            );
        } else {
            return ResponseEntity.ok(this.getUsers(oAuth2Credential)
                    .flatMap(this::getEventSubSubscriptions)
                    .flatMap(this::deleteEventSubSubscriptions)
                    .then(Mono.just("Successfully removed bot"))
            );
        }
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

    @GetMapping("/bot/add")
    public Mono<Void> authChannel(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.FOUND);

        exchange.getResponse()
                .getHeaders()
                .setLocation(URI.create(this.twitchService.getIdentityProvider()
                        .getAuthenticationUrl(
                                List.of(TwitchScopes.CHAT_CHANNEL_BOT),
                                "Twitch_AddBot_" + UUID.randomUUID()
                        ))
                );

        return exchange.getResponse()
                .setComplete();
    }

    @GetMapping("/bot/remove")
    public Mono<Void> removeBot(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.FOUND);

        exchange.getResponse()
                .getHeaders()
                .setLocation(URI.create(this.twitchService.getIdentityProvider()
                        .getAuthenticationUrl(
                                List.of(TwitchScopes.CHAT_CHANNEL_BOT),
                                "Twitch_RemoveBot_" + UUID.randomUUID()
                        ))
                );

        return exchange.getResponse()
                .setComplete();
    }

    private Flux<User> getUsers(OAuth2Credential oAuth2Credential) {
        return Mono.from(RxReactiveStreams.toPublisher(this.twitchService.getTwitchClient()
                .getUsers(oAuth2Credential.getAccessToken(), null, null)
                .toObservable()
        )).flatMapIterable(UserList::getUsers);
    }

    private Flux<EventSubSubscription> createEventSubSubscription(User user) {
        return Mono.from(RxReactiveStreams.toPublisher(this.twitchService.getTwitchClient()
                .createEventSubSubscription(
                        null,
                        SubscriptionTypes.CHANNEL_CHAT_MESSAGE.prepareSubscription(
                                builder -> builder.broadcasterUserId(user.getId())
                                        .userId(user.getId())
                                        .build(),
                                EventSubTransport.builder()
                                        .callback(this.twitchConfiguration.getEventCallbackDomain() + "/twitch/callback")
                                        .secret(this.twitchConfiguration.getEventSubSecret())
                                        .method(EventSubTransportMethod.WEBHOOK)
                                        .build()
                        )
                )
                .toObservable()
        )).flatMapIterable(EventSubSubscriptionList::getSubscriptions);
    }

    private Flux<EventSubSubscription> getEventSubSubscriptions(User user) {
        return Mono.from(RxReactiveStreams.toPublisher(this.twitchService.getTwitchClient()
                .getEventSubSubscriptions(
                        null,
                        null,
                        null,
                        user.getId(),
                        null,
                        null
                )
                .toObservable()
        )).flatMapIterable(EventSubSubscriptionList::getSubscriptions);
    }

    private Mono<Void> deleteEventSubSubscriptions(EventSubSubscription eventSubSubscription) {
        return Mono.from(RxReactiveStreams.toPublisher(this.twitchService.getTwitchClient()
                .deleteEventSubSubscription(null, eventSubSubscription.getId())
                .toObservable()
        ));
    }
}
