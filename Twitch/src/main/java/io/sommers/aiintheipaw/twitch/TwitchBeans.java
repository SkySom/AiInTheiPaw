package io.sommers.aiintheipaw.twitch;

import io.sommers.aiintheipaw.commander.command.ICommand;
import io.sommers.aiintheipaw.twitch.model.TwitchChannelProvider;
import io.sommers.aiintheipaw.twitch.model.TwitchMessageService;
import io.sommers.aiintheipaw.twitch.model.TwitchUserProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//@ConditionalOnProperty(prefix = "twitch", name = "enabled", havingValue = "true")
public class TwitchBeans {

    @Bean
    public TwitchService getTwitchService(TwitchConfiguration twitchConfiguration) {
        return new TwitchService(twitchConfiguration);
    }

    @Bean
    public TwitchCommandHandler getTwitchCommandHandler(TwitchConfiguration twitchConfiguration, TwitchMessageService twitchMessageService,
                                                        List<ICommand> commands) {
        return new TwitchCommandHandler(twitchConfiguration, commands, twitchMessageService);
    }

    @Bean
    public TwitchMessageService getTwitchMessageService(TwitchService twitchService, TwitchConfiguration twitchConfiguration,
                                                        TwitchUserProvider userProvider, MessageSource messageSource) {
        return new TwitchMessageService(twitchService, twitchConfiguration, userProvider, messageSource);
    }

    @Bean(TwitchConstants.PROVIDER)
    public TwitchChannelProvider getTwitchChannelService(TwitchMessageService twitchMessageService) {
        return new TwitchChannelProvider(twitchMessageService);
    }

    @Bean
    public TwitchUserProvider getTwitchUserService(TwitchService twitchService) {
        return new TwitchUserProvider(twitchService);
    }
}
