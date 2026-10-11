package com.Gways.gateway.filter.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class HttpMethodsConverter implements AttributeConverter<Set<GatewayRouteRetry.RetryHttpMethod>, String> {

    @Override
    public String convertToDatabaseColumn(Set<GatewayRouteRetry.RetryHttpMethod> methods) {
        if (methods == null || methods.isEmpty()) {
            return "";
        }
        return methods.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<GatewayRouteRetry.RetryHttpMethod> convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return EnumSet.noneOf(GatewayRouteRetry.RetryHttpMethod.class);
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(String::toUpperCase)
                .map(GatewayRouteRetry.RetryHttpMethod::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(GatewayRouteRetry.RetryHttpMethod.class)));
    }
}
