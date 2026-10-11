package com.Gways.controlplane.service;

import com.Gways.gateway.Registry;
import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import com.Gways.gateway.filter.service.GatewayRouteFilterService;
import com.Gways.gateway.routing.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RequestAdminService {

    private final GatewayRouteFilterService gatewayRouteFilterService;
    private final GatewayRouteService gatewayRouteService;

    private final Registry registry;

    @Transactional(readOnly = true)
    public GatewayRouteFilter findById(Long filterId) {
        return gatewayRouteFilterService.findByFilterId(filterId);
    }

    @Transactional
    public void save(GatewayRouteFilter filter) {
        if (!gatewayRouteService.existsRouteById(filter.getRoute().getId()))
            throw new IllegalArgumentException("Route no longer exists");

        filter.convertToJson();
        gatewayRouteFilterService.save(filter);
        refreshAfterCommit();
    }

    @Transactional
    public void delete(Long id) {
        gatewayRouteFilterService.deleteById(id);
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
