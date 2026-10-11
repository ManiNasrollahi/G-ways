package com.Gways.gateway.filter.service;

import com.Gways.gateway.filter.entity.GatewayRouteRetry;

public interface GatewayRouteRetryService {

    GatewayRouteRetry findById(Long id);

    void save(GatewayRouteRetry retry);

    void delete(long id);
}
