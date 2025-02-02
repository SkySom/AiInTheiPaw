package io.sommers.aiintheipaw.core.provider;

import io.sommers.aiintheipaw.core.util.ProviderId;

public interface IProvider {

    String getProvider();

    default ProviderId createId(String id) {
        return new ProviderId(this.getProvider(), id);
    }
}
