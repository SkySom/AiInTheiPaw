package io.sommers.aiintheipaw.logic;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.entity.ChannelEntity;
import io.sommers.aiintheipaw.entity.sprint.SprintEntity;
import io.sommers.aiintheipaw.entity.sprint.SprintStatusEntity;
import io.sommers.aiintheipaw.model.channel.IChannel;
import io.sommers.aiintheipaw.model.sprint.Sprint;
import io.sommers.aiintheipaw.model.sprint.SprintStatus;
import io.sommers.aiintheipaw.repository.ChannelRepository;
import io.sommers.aiintheipaw.repository.SprintRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SprintLogic {
    @Inject
    ChannelRepository channelRepository;
    @Inject
    SprintRepository sprintRepository;

    public Uni<Sprint> createSprint(
            long channelId
    ) {
        return this.channelRepository.getById(channelId)
                .flatMap(this::createSprint);
    }

    private Uni<Sprint> createSprint(ChannelEntity channel) {
        return sprintRepository.persistSprint(new SprintEntity(channel))
                .map(sprintEntity -> {
                    SprintStatusEntity sprintStatusEntity = new SprintStatusEntity();
                    sprintStatusEntity.setPreviousStatus(sprintEntity.getStatus());
                    sprintStatusEntity.setNewStatus(SprintStatus.SIGN_UP);
                    sprintEntity.setStatuses(List.of(sprintStatusEntity));
                    return sprintEntity;
                })
                .flatMap(sprintRepository::persistSprint)
                .map(this::convertSprint);

    }

    private Sprint convertSprint(SprintEntity sprintEntity) {
        return null;
    }
}
