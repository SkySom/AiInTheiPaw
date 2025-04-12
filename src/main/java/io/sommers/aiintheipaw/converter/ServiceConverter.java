package io.sommers.aiintheipaw.converter;

import io.sommers.aiintheipaw.model.service.IService;
import io.sommers.aiintheipaw.model.service.Services;
import io.vavr.collection.Map;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@ApplicationScoped
public class ServiceConverter implements AttributeConverter<IService, String> {
    @Inject
    Map<String, IService> services;

    @Override
    public String convertToDatabaseColumn(IService service) {
        return service.getName();
    }

    @Override
    public IService convertToEntityAttribute(String s) {
        return services.get(s)
                .getOrElseThrow(() -> new IllegalArgumentException("Invalid service: " + s));
    }
}
