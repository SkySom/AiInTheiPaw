package io.sommers.aiintheipaw.core.user;


import io.sommers.aiintheipaw.core.util.ProviderId;

public interface IUserSourceInfo {
    ProviderId getProviderId();

    String getName();
}
