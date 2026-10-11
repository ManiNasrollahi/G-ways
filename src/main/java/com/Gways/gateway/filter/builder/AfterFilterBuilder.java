package com.Gways.gateway.filter.builder;

import com.Gways.gateway.dto.NameDto;
import com.Gways.gateway.dto.NameValueDto;
import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.filter.AfterFilterFunctions;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import tools.jackson.databind.ObjectMapper;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class AfterFilterBuilder {

    private final ObjectMapper objectMapper;

    public BiFunction<ServerRequest, ServerResponse, ServerResponse> build(
            GatewayRouteFilter filter) {

        return switch (filter.getFilterType()) {

            case SET_RESPONSE_HEADER ->
                    setResponseHeader(filter);

            case REMOVE_RESPONSE_HEADER ->
                    removeResponseHeader(filter);

            case SET_STATUS ->
                    setStatus(filter);

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported after filter: " + filter.getFilterType()
                    );
        };
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse>
    removeResponseHeader(GatewayRouteFilter filter) {

        NameDto dto = objectMapper.readValue(
                filter.getFilterConfig(),
                NameDto.class
        );

        return AfterFilterFunctions.removeResponseHeader(
                dto.name()
        );
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse>
    setResponseHeader(GatewayRouteFilter filter) {

        NameValueDto dto = objectMapper.readValue(
                filter.getFilterConfig(),
                NameValueDto.class
        );

        return AfterFilterFunctions.setResponseHeader(
                dto.name(),
                dto.value()
        );
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse>
    setStatus(GatewayRouteFilter filter) {

        NameDto dto = objectMapper.readValue(
                filter.getFilterConfig(),
                NameDto.class
        );

        return AfterFilterFunctions.setStatus(
                HttpStatusCode.valueOf(Integer.parseInt(dto.name()))
        );
    }



}
