package com.Gways.gateway.filter.service;

import com.Gways.gateway.filter.entity.GatewayRouteFilter;

public interface GatewayRouteFilterService {

    GatewayRouteFilter findByFilterId(Long filterId);

    void save(GatewayRouteFilter gatewayRouteFilter);

    void deleteById(Long filterId);
}
