package io.sommers.aiintheipaw.model.sprint;

import io.sommers.aiintheipaw.model.channel.IChannel;

public class Sprint {
    private final long id;
    private final IChannel channel;

    private SprintStatus status;

    public Sprint(long id, IChannel channel, SprintStatus status) {
        this.id = id;
        this.channel = channel;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public IChannel getChannel() {
        return channel;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }
}
