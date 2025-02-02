package io.sommers.aiintheipaw.core.user;

import io.sommers.aiintheipaw.core.provider.IProvider;
import io.sommers.aiintheipaw.core.util.ProviderId;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserService {
    @NotEmpty
    private final Map<String, IUserProvider> userProviders;

    public UserService(List<IUserProvider> userProviders) {
        this.userProviders = userProviders.stream()
                .collect(Collectors.toMap(IProvider::getProvider, Function.identity()));
    }

    public Mono<IUser> getUser(ProviderId userId) {
        IUserProvider userProvider = this.userProviders.get(userId.provider());

        if (userProvider != null) {
            return userProvider.getUser(userId.id());
        } else {
            return Mono.error(new IllegalArgumentException("No channel found for provider id " + userId.id()));
        }
    }
}
