package io.sommers.ai.discord.role;

import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Guild;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.List;

public class RoleCommandAdaptor extends ReactiveEventAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleCommandAdaptor.class);

    private final RestClient restClient;

    public RoleCommandAdaptor(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @NonNull
    public Flux<ApplicationCommandData> onReady(@NonNull final ReadyEvent event) {
        ApplicationCommandRequest role = ApplicationCommandRequest.builder()
                .name("role")
                .description("Command for user selection of Roles")
                .addAllOptions(List.of(
                        ApplicationCommandOptionData.builder()
                                .name("manage")
                                .description("Manage your roles")
                                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                                .build(),
                        ApplicationCommandOptionData.builder()
                                .name("list")
                                .description("List available roles for selection")
                                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                                .build(),
                        ApplicationCommandOptionData.builder()
                                .name("add")
                                .description("Add a Role")
                                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                                .addOption(ApplicationCommandOptionData.builder()
                                        .name("role")
                                        .description("Role to add")
                                        .type(ApplicationCommandOption.Type.STRING.getValue())
                                        .autocomplete(true)
                                        .required(true)
                                        .build()
                                )
                                .build(),
                        ApplicationCommandOptionData.builder()
                                .name("remove")
                                .description("Remove a Role")
                                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                                .addOption(ApplicationCommandOptionData.builder()
                                        .name("role")
                                        .description("Role to remove")
                                        .type(ApplicationCommandOption.Type.STRING.getValue())
                                        .autocomplete(true)
                                        .required(true)
                                        .build()
                                )
                                .build()
                ))
                .build();

        ApplicationCommandRequest roleAdmin = ApplicationCommandRequest.builder()
                .name("role-admin")
                .description("Command for admin handling of Roles")
                .addAllOptions(List.of(
                        ApplicationCommandOptionData.builder()
                                .name("add")
                                .description("Add a Role to the Selection")
                                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                                .addOption(ApplicationCommandOptionData.builder()
                                        .name("role")
                                        .description("Role to add")
                                        .type(ApplicationCommandOption.Type.ROLE.getValue())
                                        .autocomplete(true)
                                        .build()
                                )
                                .build(),
                        ApplicationCommandOptionData.builder()
                                .name("remove")
                                .description("Remove a Role to the Selection")
                                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                                .addOption(ApplicationCommandOptionData.builder()
                                        .name("role")
                                        .description("Role to remove")
                                        .type(ApplicationCommandOption.Type.ROLE.getValue())
                                        .autocomplete(true)
                                        .build()
                                )
                                .build()
                ))
                .build();

        List<ApplicationCommandRequest> requests = List.of(role, roleAdmin);

        return this.restClient.getApplicationId()
                .flatMapMany(applicationId -> Flux.fromIterable(event.getGuilds())
                        .map(ReadyEvent.Guild::getId)
                        .flatMap(guildId -> this.restClient.getApplicationService()
                                .bulkOverwriteGuildApplicationCommand(
                                        applicationId,
                                        guildId.asLong(),
                                        requests
                                )
                        )
                )
                .doOnError(error -> LOGGER.error("Failed to update Guild Application Commands", error));
    }

    @Override
    @NonNull
    public Publisher<?> onChatInputInteraction(@NonNull final ChatInputInteractionEvent event) {
        if (event.getOption("manage").isPresent()) {
            return event.deferReply()
                    .withEphemeral(true)
                    .then(event.createFollowup()
                            .withEmbeds(EmbedCreateSpec.builder()
                                    .title("Manage Roles")
                                    .description("Manage your roles")
                                    .addField(EmbedCreateFields.Field.of(
                                            "test",
                                            "test <:GalaxyBolb:1304687345190305833>",
                                            false
                                    ))
                                    .build()
                            )
                            .withComponents(ActionRow.of(
                                    SelectMenu.ofRole("role")
                                            .withMinValues(1)
                                            .withMaxValues(10)
                            ))
                    );
        } else {
            return event.reply()
                    .withEphemeral(true)
                    .withContent(event.getOption("add")
                            .flatMap(option -> option.getOption("role"))
                            .flatMap(option -> option.getValue()
                                    .map(ApplicationCommandInteractionOptionValue::asString)
                                    .map(value -> option.getName() + " " + value)
                            )
                            .orElse("Nothing")
                    );
        }
    }

    @Override
    @NonNull
    public Publisher<?> onChatInputAutoCompleteInteraction(@NonNull ChatInputAutoCompleteEvent event) {
        if (event.getCommandName().startsWith("role")) {
            if (event.getFocusedOption().getName().equals("role")) {
                return event.getInteraction()
                        .getGuild()
                        .flatMapMany(Guild::getRoles)
                        .<ApplicationCommandOptionChoiceData>map(role -> ApplicationCommandOptionChoiceData.builder()
                                .name(role.getName())
                                .value(role.getId().asLong())
                                .build()
                        )
                        .collectList()
                        .map(event::respondWithSuggestions);
            }

        }
        return Mono.empty();
    }
}
