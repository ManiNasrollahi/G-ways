package com.Gways.gateway.filter.builder;

import com.Gways.gateway.dto.NameDto;
import com.Gways.gateway.dto.NameValueDto;
import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class BeforeFilterBuilder {

    private final ObjectMapper objectMapper;

    public Function<ServerRequest, ServerRequest> build(GatewayRouteFilter filter) {
        return switch (filter.getFilterType()){
            case SET_REQUEST_HEADER -> setRequestHeader(filter);
            case REMOVE_REQUEST_HEADER -> removeRequestHeader(filter);
            case ADD_REQUEST_PARAMETER -> addRequestParameter(filter);
            case REMOVE_REQUEST_PARAMETER -> removeRequestParameter(filter);
            case SET_PATH -> setPath(filter);
            case PREFIX_PATH -> prefixPath(filter);
            case STRIP_PREFIX -> stripPrefix(filter);
            case REQUEST_SIZE -> requestSize(filter);
            default -> null;
        };
    }

    private Function<ServerRequest, ServerRequest> setRequestHeader(GatewayRouteFilter filter) {
        NameValueDto dto = objectMapper.readValue(filter.getFilterConfig(), NameValueDto.class);
        return BeforeFilterFunctions.setRequestHeader(dto.name(), dto.value());
    }

    private Function<ServerRequest, ServerRequest> removeRequestHeader(GatewayRouteFilter filter) {
        NameDto dto = objectMapper.readValue(filter.getFilterConfig(), NameDto.class);
        return BeforeFilterFunctions.removeRequestHeader(dto.name());
    }

    private Function<ServerRequest, ServerRequest> addRequestParameter(GatewayRouteFilter filter) {
        NameValueDto dto = objectMapper.readValue(filter.getFilterConfig(), NameValueDto.class);
        return BeforeFilterFunctions.addRequestParameter(dto.name(), dto.value());
    }

    private Function<ServerRequest, ServerRequest> removeRequestParameter(GatewayRouteFilter filter) {
        NameDto dto = objectMapper.readValue(
                filter.getFilterConfig(),
                NameDto.class
        );
        return BeforeFilterFunctions.removeRequestParameter(dto.name());
    }

    private Function<ServerRequest, ServerRequest> requestSize(
            GatewayRouteFilter filter) {

        NameDto dto = objectMapper.readValue(
                filter.getFilterConfig(),
                NameDto.class
        );

        return BeforeFilterFunctions.requestSize(dto.name());
    }

    private Function<ServerRequest, ServerRequest> setPath(GatewayRouteFilter filter) {
        NameDto dto = objectMapper.readValue(filter.getFilterConfig(), NameDto.class);
        return BeforeFilterFunctions.setPath(dto.name());
    }

    private Function<ServerRequest, ServerRequest> prefixPath(GatewayRouteFilter filter) {
        NameDto dto = objectMapper.readValue(filter.getFilterConfig(), NameDto.class);
        return BeforeFilterFunctions.prefixPath(dto.name());
    }

    private Function<ServerRequest, ServerRequest> stripPrefix(GatewayRouteFilter filter) {
        NameDto dto = objectMapper.readValue(filter.getFilterConfig(), NameDto.class);
        return BeforeFilterFunctions.stripPrefix(Integer.parseInt(dto.name()));
    }
}
