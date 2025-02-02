package io.sommers.aiintheipaw.core.user;


import io.sommers.aiintheipaw.core.util.ProviderId;

public interface IUser {
    ProviderId getProviderId();

    String getName();
}
