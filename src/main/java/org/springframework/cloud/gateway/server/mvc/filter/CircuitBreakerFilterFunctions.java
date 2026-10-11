package org.springframework.cloud.gateway.server.mvc.filter;

import com.Gways.configuration.CircuitBreakerProvider;
import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.gateway.server.mvc.common.Configurable;
import org.springframework.cloud.gateway.server.mvc.common.HttpStatusHolder;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.cloud.gateway.server.mvc.common.Shortcut;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public abstract class CircuitBreakerFilterFunctions {

    private CircuitBreakerFilterFunctions() {
    }


    public static HandlerFilterFunction<ServerResponse, ServerResponse> circuitBreaker(Consumer<CircuitBreakerConfig> configConsumer, GatewayRouteCircuitBreaker routeCircuitBreaker) {
        CircuitBreakerConfig config = new CircuitBreakerConfig();
        configConsumer.accept(config);
        return circuitBreaker(config, routeCircuitBreaker);
    }

    @Shortcut({"id"})
    @Configurable
    public static HandlerFilterFunction<ServerResponse, ServerResponse> circuitBreaker(CircuitBreakerConfig config, GatewayRouteCircuitBreaker routeCircuitBreaker) {
        if (routeCircuitBreaker == null) {
            return null;
        }
        Set<HttpStatusCode> failureStatuses = (Set)config.getStatusCodes().stream().map((status) -> HttpStatusHolder.valueOf(status).resolve()).collect(Collectors.toSet());
        CircuitBreakerFactory<?, ?> circuitBreakerFactory = CircuitBreakerProvider.getR4J(routeCircuitBreaker);
        return (request, next) -> {
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create(config.getId());
            return (ServerResponse)circuitBreaker.run(() -> {
                try {
                    ServerResponse serverResponse = next.handle(request);
                    if (failureStatuses.contains(serverResponse.statusCode())) {
                        throw new CircuitBreakerStatusCodeException(serverResponse.statusCode());
                    } else {
                        return serverResponse;
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, (throwable) -> {
                if (!StringUtils.hasText(config.getFallbackPath())) {
                    if (throwable instanceof TimeoutException) {
                        throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, throwable.getMessage(), throwable);
                    } else if (throwable instanceof CallNotPermittedException) {
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, throwable.getMessage(), throwable);
                    } else if (config.isResumeWithoutError()) {
                        return ServerResponse.ok().build();
                    } else {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable != null ? throwable.getMessage() : null, throwable);
                    }
                } else {
                    MvcUtils.putAttribute(request, MvcUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, throwable);
                    return GatewayServerResponse.ok().build((httpServletRequest, httpServletResponse) -> {
                        try {
                            String expandedFallback = MvcUtils.expand(request, config.getFallbackPath() != null ? config.getFallbackPath() : "");
                            request.servletRequest().getServletContext().getRequestDispatcher(expandedFallback).forward(httpServletRequest, httpServletResponse);
                            return null;
                        } catch (IOException | ServletException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        };
    }

    public static class CircuitBreakerConfig {
        private @Nullable String id;
        private @Nullable String fallbackPath;
        private Set<String> statusCodes = new HashSet();
        private boolean resumeWithoutError = false;

        public @Nullable String getId() {
            return this.id;
        }

        public CircuitBreakerConfig setId(String id) {
            this.id = id;
            return this;
        }

        public @Nullable String getFallbackPath() {
            return this.fallbackPath;
        }

        public CircuitBreakerConfig setFallbackUri(String fallbackUri) {
            Objects.requireNonNull(fallbackUri, "fallbackUri String may not be null");
            this.setFallbackUri(URI.create(fallbackUri));
            return this;
        }

        public CircuitBreakerConfig setFallbackUri(@Nullable URI fallbackUri) {
            if (fallbackUri != null) {
                Assert.isTrue(fallbackUri.getScheme().equalsIgnoreCase("forward"), () -> "Scheme must be forward, but is " + fallbackUri.getScheme());
                this.fallbackPath = fallbackUri.getPath();
            } else {
                this.fallbackPath = null;
            }

            return this;
        }

        public CircuitBreakerConfig setFallbackPath(String fallbackPath) {
            this.fallbackPath = fallbackPath;
            return this;
        }

        public Set<String> getStatusCodes() {
            return this.statusCodes;
        }

        public CircuitBreakerConfig setStatusCodes(String... statusCodes) {
            return this.setStatusCodes((Set)(new LinkedHashSet(Arrays.asList(statusCodes))));
        }

        public CircuitBreakerConfig setStatusCodes(Set<String> statusCodes) {
            this.statusCodes = statusCodes;
            return this;
        }

        public boolean isResumeWithoutError() {
            return this.resumeWithoutError;
        }

        public CircuitBreakerConfig setResumeWithoutError(boolean resumeWithoutError) {
            this.resumeWithoutError = resumeWithoutError;
            return this;
        }
    }

    public static class CircuitBreakerStatusCodeException extends ResponseStatusException {
        public CircuitBreakerStatusCodeException(HttpStatusCode statusCode) {
            super(statusCode);
        }
    }

    public static class FilterSupplier extends SimpleFilterSupplier {
        public FilterSupplier() {
            super(CircuitBreakerFilterFunctions.class);
        }
    }
}
