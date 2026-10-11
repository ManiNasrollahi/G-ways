package com.Gways.gateway.filter.service;

import com.Gways.gateway.filter.entity.GatewayRouteRateLimit;

public interface GatewayRouteRateLimitService {

    GatewayRouteRateLimit findById(Long id);

    void save(GatewayRouteRateLimit gatewayRouteRateLimit);

    void delete(long id);
}
