package com.Gways.controlplane.service;

import com.Gways.gateway.Registry;
import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;
import com.Gways.gateway.filter.service.GatewayRouteCircuitBreakerService;
import com.Gways.gateway.routing.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RouteCircuitBreakerAdminService {

    private final GatewayRouteCircuitBreakerService circuitBreakerService;
    private final GatewayRouteService gatewayRouteService;
    private final Registry registry;

    @Transactional(readOnly = true)
    public GatewayRouteCircuitBreaker findById(Long id) {
        return circuitBreakerService.findById(id);
    }

    @Transactional
    public void save(GatewayRouteCircuitBreaker circuitBreaker) {
        if (!gatewayRouteService.existsRouteById(circuitBreaker.getRoute().getId())) {
            throw new IllegalArgumentException("Route no longer exists");
        }

        try {
            circuitBreakerService.save(circuitBreaker);
            refreshAfterCommit();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("This route already has a circuit breaker");
        }
    }

    @Transactional
    public void delete(long id) {
        circuitBreakerService.delete(id);
        refreshAfterCommit();
    }

    private void refreshAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                registry.initialize();
            }
        });
    }
}
