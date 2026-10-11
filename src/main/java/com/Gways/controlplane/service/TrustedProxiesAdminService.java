package com.Gways.controlplane.service;

import com.Gways.configuration.RuntimeConfiguration;
import com.Gways.gateway.Registry;
import com.Gways.gateway.trustedProxies.entity.GatewayTrustedProxy;
import com.Gways.gateway.trustedProxies.service.GatewayTrustedProxiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrustedProxiesAdminService {

    private final GatewayTrustedProxiesService gatewayTrustedProxiesService;
    private final RuntimeConfiguration runtimeConfiguration;
    private final Registry registry;

    @Transactional(readOnly = true)
    public List<GatewayTrustedProxy> findAllTrustedProxies() {
        return gatewayTrustedProxiesService.findAllTrustedProxies();
    }

    @Transactional(readOnly = true)
    public GatewayTrustedProxy findById(Long id) {
        return gatewayTrustedProxiesService.findById(id);
    }

    @Transactional
    public void save(GatewayTrustedProxy gatewayTrustedProxy) {
        gatewayTrustedProxiesService.save(gatewayTrustedProxy);
        refreshAfterCommit();
    }

    @Transactional
    public void deleteById(Long id) {
        gatewayTrustedProxiesService.deleteById(id);
        refreshAfterCommit();
    }

    private void refreshAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                runtimeConfiguration.refreshTrustedProxies();
                registry.initialize();
            }
        });
    }
}
