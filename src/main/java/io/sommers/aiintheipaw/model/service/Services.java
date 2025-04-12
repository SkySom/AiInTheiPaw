package io.sommers.aiintheipaw.model.service;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;

import java.util.function.Function;


@ApplicationScoped
public class Services {

    @Produces
    @ApplicationScoped
    IService twitch() {
        return new Service("twitch");
    }

    @Produces
    @ApplicationScoped
    public Map<String, IService> services(Instance<IService> services) {
        return Stream.ofAll(services)
                .collect(HashMap.collector(IService::getName, Function.identity()));
    }
}
