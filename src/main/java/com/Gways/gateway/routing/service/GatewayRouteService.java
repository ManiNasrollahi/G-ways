package com.Gways.gateway.routing.service;

import com.Gways.gateway.routing.entity.GatewayRoute;

import java.util.List;

public interface GatewayRouteService {

    GatewayRoute findRouteById(long id);

    List<GatewayRoute> findAllRoutes();

    List<GatewayRoute> findAllByEnabledTrueOrderByRouteOrderAsc();

    void deleteRouteById(long id);

    void saveRoute(GatewayRoute route);

    boolean existsRouteById(long id);
}
