package io.sommers.ai.twitch;

import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.twitch.model.TwitchChannelService;
import io.sommers.ai.twitch.model.TwitchMessageService;
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
    public TwitchCommandHandler getTwitchCommandHandler(TwitchConfiguration twitchConfiguration, TwitchMessageService twitchMessageService, List<ICommand> commands) {
        return new TwitchCommandHandler(twitchConfiguration, commands, twitchMessageService);
    }

    @Bean
    public TwitchMessageService getTwitchMessageService(TwitchService twitchService, TwitchConfiguration twitchConfiguration) {
        return new TwitchMessageService(twitchService, twitchConfiguration);
    }

    @Bean("twitch")
    public TwitchChannelService getTwitchChannelService(TwitchMessageService twitchMessageService) {
        return new TwitchChannelService(twitchMessageService);
    }
}
