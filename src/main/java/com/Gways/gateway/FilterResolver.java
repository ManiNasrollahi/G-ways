package com.Gways.gateway;

import com.Gways.configuration.IpResolver;
import com.Gways.configuration.RuntimeConfiguration;
import com.Gways.gateway.filter.builder.AfterFilterBuilder;
import com.Gways.gateway.filter.builder.BeforeFilterBuilder;
import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import com.Gways.gateway.filter.entity.GatewayRouteRateLimit;
import com.Gways.gateway.filter.entity.GatewayRouteRetry;
import com.Gways.gateway.routing.entity.GatewayRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions.rateLimit;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.RetryFilterFunctions.retry;

@Component
@RequiredArgsConstructor
public class FilterResolver {

    private final BeforeFilterBuilder beforeFilterBuilder;
    private final AfterFilterBuilder afterFilterBuilder;
    private final RuntimeConfiguration runtimeConfiguration;
    private final IpResolver ipResolver;

    public Function<ServerRequest, ServerRequest> beforeResolver(GatewayRouteFilter filter) {
        return beforeFilterBuilder
                .build(filter);
    }

    public BiFunction<ServerRequest, ServerResponse, ServerResponse> afterResolver(GatewayRouteFilter filter) {
        return afterFilterBuilder
                .build(filter);
    }

    public HandlerFilterFunction<ServerResponse, ServerResponse> rateLimitResolver(GatewayRouteRateLimit filter) {
        HandlerFilterFunction<ServerResponse, ServerResponse> limiter = rateLimit(
                config -> config.setPeriod(Duration.ofSeconds(filter.getPeriod()))
                        .setCapacity(filter.getCapacity())
                        .setKeyResolver(request -> ipResolver.resolveClientIp(request, runtimeConfiguration.getTrustedProxiesRegex()))
        );
        return (request, next) -> {
            boolean[] forwarded = {false};
            ServerResponse response = limiter.filter(request, accepted -> {
                forwarded[0] = true;
                return next.handle(accepted);
            });
            if (!forwarded[0] && response.statusCode().value() == 429) {
                return ServerResponse.status(429)
                        .headers(headers -> headers.addAll(response.headers()))
                        .body("""
                            {
                                "status": 429,
                                "error": "rateLimit",
                                "message": "Client IP is Rate Limited"
                            }
                        """);
            }
            return response;
        };
    }

    public HandlerFilterFunction<ServerResponse, ServerResponse> retryResolver(GatewayRouteRetry retry) {
        return retry(
                retryConfig -> retryConfig.setRetries(retry.getRetryCount())
                        .setExceptions(Set.of(IOException.class, TimeoutException.class, ResourceAccessException.class))
                        .setSeries(Set.of(HttpStatus.Series.SERVER_ERROR))
                        .setMethods(
                                retry.getMethods().stream()
                                        .map(GatewayRouteRetry.RetryHttpMethod::toHttpMethod)
                                        .collect(Collectors.toSet())
                        )
                        .setBackoff(
                                Duration.ofSeconds(retry.getFirstBackoffSeconds()),
                                Duration.ofSeconds(retry.getMaxBackoffSeconds()),
                                retry.getBackoffFactor()
                        )
        );
    }

    public HandlerFilterFunction<ServerResponse, ServerResponse> circuitBreakerResolver(GatewayRoute gatewayRoute) {
        return circuitBreaker(
                circuitBreakerConfig ->
                        circuitBreakerConfig.setId(gatewayRoute.getRouteId()+"-"+"CB")
                                .setStatusCodes(
                                        gatewayRoute.getCircuitBreaker().getHttpStatusList()
                                        .stream().map(String::valueOf).collect(Collectors.toSet())
                                )
                                .setFallbackUri("forward:" + gatewayRoute.getCircuitBreaker().getFallBackUri()),
                gatewayRoute.getCircuitBreaker()
        );
    }


}
