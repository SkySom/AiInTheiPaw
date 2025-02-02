package io.sommers.aiintheipaw.commands;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class CommandBeans {
    @Bean
    public MessageSource commandMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages/sprint-messages.properties");
        return messageSource;
    }
}
