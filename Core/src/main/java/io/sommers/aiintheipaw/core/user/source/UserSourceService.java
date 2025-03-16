package io.sommers.aiintheipaw.core.user.source;

import io.sommers.aiintheipaw.core.user.IUserRepository;
import io.sommers.aiintheipaw.core.user.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserSourceService {

    private final IUserSourceRepository userSourceRepository;
    private final IUserRepository userRepository;

    public UserSourceService(IUserSourceRepository userSourceRepository, IUserRepository userRepository) {
        this.userSourceRepository = userSourceRepository;
        this.userRepository = userRepository;
    }

    @Cacheable("user_sources")
    public Mono<UserSource> findByServiceAndId(String service, String id) {
        return this.userSourceRepository.findUserSourceByServiceAndServiceId(service, id)
                .switchIfEmpty(Mono.defer(() -> this.userRepository.save(new User())
                        .flatMap(user -> this.userSourceRepository.save(new UserSource(service, id)))
                ));
    }
}
