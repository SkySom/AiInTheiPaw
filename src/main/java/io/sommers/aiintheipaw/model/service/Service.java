package io.sommers.aiintheipaw.model.service;

public record Service(
        String name
) implements IService{

    @Override
    public String getName() {
        return this.name();
    }
}
