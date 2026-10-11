package com.Gways.controlplane.service;

import com.Gways.gateway.Registry;
import com.Gways.gateway.filter.entity.GatewayRouteRateLimit;
import com.Gways.gateway.filter.service.GatewayRouteRateLimitService;
import com.Gways.gateway.routing.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RouteRateLimitAdminService {

    private final GatewayRouteRateLimitService gatewayRouteRateLimitService;
    private final GatewayRouteService gatewayRouteService;
    private final Registry registry;

    @Transactional(readOnly = true)
    public GatewayRouteRateLimit findById(long id){
        return gatewayRouteRateLimitService.findById(id);
    }

    @Transactional
    public void save(GatewayRouteRateLimit gatewayRouteRateLimit){
        if (!gatewayRouteService.existsRouteById(gatewayRouteRateLimit.getRoute().getId()))
            throw new IllegalArgumentException("Route no longer exists");
        try {
            gatewayRouteRateLimitService.save(gatewayRouteRateLimit);
            refreshAfterCommit();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("This route already has a rate limit");
        }
    }

    @Transactional
    public void delete(long id){
        gatewayRouteRateLimitService.delete(id);
        refreshAfterCommit();
    }

    private void refreshAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                registry.initialize();
            }
        });
    }
}
