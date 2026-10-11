package com.Gways.gateway;

import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import com.Gways.gateway.predicate.entity.GatewayRoutePredicate;
import com.Gways.gateway.routing.entity.GatewayRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.*;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Component
@RequiredArgsConstructor
public class RouteBuilder {

    private final PredicateResolver predicateResolver;
    private final FilterResolver filterResolver;

    public List<RouterFunction<ServerResponse>> build(List<GatewayRoute> gatewayRoutes) {
        List<RouterFunction<ServerResponse>> routes = new ArrayList<>();

        gatewayRoutes.forEach(r -> {

            RouterFunctions.Builder route = route(r.getRouteId())
                    .route(addPredicate(r), http())

                    .before(request -> {
                        String requestId = resolveRequestId(request);
                        return ServerRequest.from(request)
                                .header("X-Request-ID", requestId)
                                .build();
                    });

            if (r.hasFilters(GatewayRouteFilter.FilterPhase.BEFORE))
                route.before(addBeforeFilter(r));

            if (r.hasRateLimiter())
                route.filter(addRateLimitFilter(r));

            if (r.hasRetry())
                route.filter(addRetryFilter(r));

            if (r.hasCircuitBreaker())
                route.filter(addCircuitBreakerFilter(r));

            if (r.hasFilters(GatewayRouteFilter.FilterPhase.AFTER))
                route.after(addAfterFilter(r));

            route.before(request -> uri(r.getUri()).apply(request));

            routes.add(route.build());

        });
        return routes;
    }

    private RequestPredicate addPredicate(GatewayRoute r) {
        var enabled = r.getPredicates().stream().filter(GatewayRoutePredicate::getEnabled).toList();
        if (enabled.isEmpty()) throw new RuntimeException("cannot find enabled predicate");
        var predicate = predicateResolver.resolve(enabled.getFirst());
        for (int i = 1; i < enabled.size(); i++) {
            var next = predicateResolver.resolve(enabled.get(i));
            predicate = predicate.and(next);
        }
        return predicate;
    }

    private Function<ServerRequest, ServerRequest> addBeforeFilter(GatewayRoute r) {
        var enabled = r.getFilters().stream().filter(f -> Boolean.TRUE.equals(f.getEnabled()) && f.getFilterType().getPhase() == GatewayRouteFilter.FilterPhase.BEFORE).toList();
        if (enabled.isEmpty()) throw new RuntimeException("cannot find enabled filters");
        var filter = filterResolver.beforeResolver(enabled.getFirst());
        for (int i = 1; i < enabled.size(); i++) {
            var next = filterResolver.beforeResolver(enabled.get(i));
            filter = filter.andThen(next);
        }
        return filter;
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse> addAfterFilter(GatewayRoute r) {
        var enabled = r.getFilters().stream().filter(f -> Boolean.TRUE.equals(f.getEnabled()) && f.getFilterType().getPhase() == GatewayRouteFilter.FilterPhase.AFTER).toList();
        if (enabled.isEmpty()) throw new RuntimeException("cannot find enabled filters");
        var filter = filterResolver.afterResolver(enabled.getFirst());
        for (int i = 1; i < enabled.size(); i++) {
            var next = filterResolver.afterResolver(enabled.get(i));

            var current = filter;

            filter = (request, response) ->
                    next.apply(request, current.apply(request, response));
        }
        return filter;
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> addRateLimitFilter(GatewayRoute r) {
        return filterResolver.rateLimitResolver(r.getRateLimits());
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> addRetryFilter(GatewayRoute r) {
        return filterResolver.retryResolver(r.getRetry());
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> addCircuitBreakerFilter(GatewayRoute r) {
        return filterResolver.circuitBreakerResolver(r);
    }

    private String resolveRequestId(ServerRequest request) {

        String requestId =
                request.headers().firstHeader("X-Request-ID");

        if (requestId != null && !requestId.isBlank()) {
            return requestId.trim();
        }

        return UUID.randomUUID().toString();
    }
}
