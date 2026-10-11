package com.Gways.gateway;

import com.Gways.gateway.dto.BetweenDto;
import com.Gways.gateway.dto.NameValueDto;
import com.Gways.gateway.dto.PatternsDto;
import com.Gways.gateway.dto.TimeDto;
import com.Gways.gateway.predicate.entity.GatewayRoutePredicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RequestPredicate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.*;

@Component
@RequiredArgsConstructor
public class PredicateResolver {

    private final ObjectMapper objectMapper;

    public RequestPredicate resolve(GatewayRoutePredicate predicate) {
        return switch (predicate.getPredicateType()){
            case PATH -> pathResolver(predicate);
            case METHOD -> methodResolver(predicate);
            case HEADER -> headerResolver(predicate);
            case HOST -> hostResolver(predicate);
            case QUERY -> queryResolver(predicate);
            case COOKIE -> cookieResolver(predicate);
            case BEFORE -> beforeResolver(predicate);
            case AFTER -> afterResolver(predicate);
            case BETWEEN -> betweenResolver(predicate);
        };
    }

    private RequestPredicate pathResolver(GatewayRoutePredicate predicate) {
        PatternsDto dto = objectMapper.readValue(predicate.getPredicateConfig(), PatternsDto.class);
        return path(dto.patterns().toArray(new String[0]));
    }

    private RequestPredicate methodResolver(GatewayRoutePredicate predicate) {
        PatternsDto dto = objectMapper.readValue(predicate.getPredicateConfig(), PatternsDto.class);
        List<HttpMethod> methods = dto.patterns().stream().map(HttpMethod::valueOf).toList();
        return method(methods.toArray(new HttpMethod[0]));
    }

    private RequestPredicate headerResolver(GatewayRoutePredicate predicate) {
        NameValueDto dto = objectMapper.readValue(predicate.getPredicateConfig(), NameValueDto.class);
        return header(dto.name(), dto.value());
    }

    private RequestPredicate hostResolver(GatewayRoutePredicate predicate) {
        PatternsDto dto = objectMapper.readValue(predicate.getPredicateConfig(), PatternsDto.class);
        return host(dto.patterns().toArray(new String[0]));
    }

    private RequestPredicate queryResolver(GatewayRoutePredicate predicate) {
        NameValueDto dto = objectMapper.readValue(predicate.getPredicateConfig(), NameValueDto.class);
        return query(dto.name(), dto.value());
    }

    private RequestPredicate cookieResolver(GatewayRoutePredicate predicate) {
        NameValueDto dto = objectMapper.readValue(predicate.getPredicateConfig(), NameValueDto.class);
        return cookie(dto.name(), dto.value());
    }

    private RequestPredicate beforeResolver(GatewayRoutePredicate predicate) {
        TimeDto dto = objectMapper.readValue(predicate.getPredicateConfig(), TimeDto.class);
        return before(dto.dateTime());
    }

    private RequestPredicate afterResolver(GatewayRoutePredicate predicate) {
        TimeDto dto = objectMapper.readValue(predicate.getPredicateConfig(), TimeDto.class);
        return after(dto.dateTime());
    }

    private RequestPredicate betweenResolver(GatewayRoutePredicate predicate) {
        BetweenDto dto = objectMapper.readValue(predicate.getPredicateConfig(), BetweenDto.class);
        return between(dto.dateTime1(), dto.dateTime2());
    }

}
