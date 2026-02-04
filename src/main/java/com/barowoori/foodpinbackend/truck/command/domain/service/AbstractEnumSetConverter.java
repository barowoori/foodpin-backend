package com.barowoori.foodpinbackend.truck.command.domain.service;

import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractEnumSetConverter<E extends Enum<E>>
        implements AttributeConverter<Set<E>, String> {

    private static final String DELIMITER = ",";

    private final Class<E> enumClass;

    protected AbstractEnumSetConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(Set<E> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<E> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(dbData.split(DELIMITER))
                .map(value -> Enum.valueOf(enumClass, value))
                .collect(Collectors.toSet());
    }
}