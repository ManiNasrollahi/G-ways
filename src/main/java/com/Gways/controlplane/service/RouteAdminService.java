package com.Gways.controlplane.service;

import com.Gways.gateway.Registry;
import com.Gways.gateway.dto.ServerStatus;
import com.Gways.gateway.predicate.entity.GatewayRoutePredicate;
import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.routing.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteAdminService {

    private final GatewayRouteService gatewayRouteService;
    private final CheckRoutes checkRoutes;
    private final Registry registry;

    @Transactional(readOnly = true)
    public List<GatewayRoute> findAll() {
        return gatewayRouteService.findAllRoutes();
    }

    @Transactional(readOnly = true)
    public GatewayRoute findById(Long id) {
        return gatewayRouteService.findRouteById(id);
    }

    @Transactional
    public void deleteRouteById(Long id) {
        gatewayRouteService.deleteRouteById(id);
        refreshGatewayAfterCommit();
    }

    @Transactional
    public void save(GatewayRoute route) {
        route.getPredicates()
                .forEach(GatewayRoutePredicate::convertToJson);
        gatewayRouteService.saveRoute(route);
        refreshGatewayAfterCommit();
    }

    public Map<String, ServerStatus> checkServers(List<GatewayRoute> routes){
        return checkRoutes.checkServers(routes);
    }

    private void refreshGatewayAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { registry.initialize(); }
        });
    }



}
