package com.Gways.gateway.filter.service;

import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;

public interface GatewayRouteCircuitBreakerService {

    GatewayRouteCircuitBreaker findById(Long id);

    void save(GatewayRouteCircuitBreaker circuitBreaker);

    void delete(long id);
}
