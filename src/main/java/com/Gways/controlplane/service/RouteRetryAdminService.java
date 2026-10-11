package com.Gways.controlplane.service;

import com.Gways.gateway.Registry;
import com.Gways.gateway.filter.entity.GatewayRouteRetry;
import com.Gways.gateway.filter.service.GatewayRouteRetryService;
import com.Gways.gateway.routing.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RouteRetryAdminService {

    private final GatewayRouteRetryService retryService;
    private final GatewayRouteService gatewayRouteService;

    private final Registry registry;

    @Transactional(readOnly = true)
    public GatewayRouteRetry findById(long id) {
        return retryService.findById(id);
    }

    @Transactional
    public void save(GatewayRouteRetry retry) {
        if (!gatewayRouteService.existsRouteById(retry.getRoute().getId()))
            throw new IllegalArgumentException("Route no longer exists");
        try {
            retryService.save(retry);
            refreshAfterCommit();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("This route already has a retry");
        }
    }

    @Transactional
    public void delete(long id) {
        retryService.delete(id);
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
