package io.sommers.ai.model.user;

import io.sommers.ai.model.ProviderId;

public interface IUser {
    ProviderId getProviderId();

    String getName();
}
