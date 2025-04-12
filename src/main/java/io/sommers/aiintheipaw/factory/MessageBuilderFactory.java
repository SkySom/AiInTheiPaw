package io.sommers.aiintheipaw.factory;

import io.sommers.aiintheipaw.model.messagebuilder.MessageBuilder;
import io.sommers.aiintheipaw.model.service.IService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageBuilderFactory {

    public MessageBuilder create(IService service) {
        return new MessageBuilder(service);
    }
}
