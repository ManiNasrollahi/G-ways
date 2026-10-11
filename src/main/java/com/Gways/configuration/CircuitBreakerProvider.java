package com.Gways.configuration;

import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;

import java.time.Duration;

public class CircuitBreakerProvider {

    public static Resilience4JCircuitBreakerFactory getR4J(GatewayRouteCircuitBreaker cb) {

        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(
                        CircuitBreakerConfig.custom()
                                .minimumNumberOfCalls(cb.getMinimumNumberOfCalls())
                                .slidingWindowSize(cb.getSlidingWindowSize())
                                .failureRateThreshold(cb.getFailureRateThreshold())
                                .waitDurationInOpenState(Duration.ofSeconds(cb.getWaitDurationInOpenStateSeconds()))
                                .permittedNumberOfCallsInHalfOpenState(cb.getPermittedNumberOfCallsInHalfOpenState())
                                .build()
                );

        TimeLimiterRegistry timeLimiterRegistry =
                TimeLimiterRegistry.of(
                        TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(cb.getTimeOutInSeconds()))
                                .build()
                );

        return
                new Resilience4JCircuitBreakerFactory(
                        circuitBreakerRegistry,
                        timeLimiterRegistry,
                        null
                );

    }



}
