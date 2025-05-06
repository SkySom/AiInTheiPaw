package io.sommers.aiintheipaw.routing;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.logic.SprintLogic;
import io.sommers.aiintheipaw.model.sprint.CreateSprintRequest;
import io.sommers.aiintheipaw.model.sprint.Sprint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("sprint")
@ApplicationScoped
public class SprintRoutes {
    @Inject
    SprintLogic sprintLogic;

    @POST
    public Uni<Sprint> createSprint(@Valid CreateSprintRequest request) {
        return this.sprintLogic.createSprint(request.channelId());
    }
}
