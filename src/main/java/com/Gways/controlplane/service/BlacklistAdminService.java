package com.Gways.controlplane.service;

import com.Gways.configuration.RuntimeConfiguration;
import com.Gways.gateway.blackList.entity.GatewayBlacklist;
import com.Gways.gateway.blackList.service.GatewayBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistAdminService {

    private final GatewayBlacklistService gatewayBlacklistService;
    private final RuntimeConfiguration runtimeConfiguration;

    @Transactional(readOnly = true)
    public List<GatewayBlacklist> findAll() {
        return gatewayBlacklistService.findAll();
    }

    @Transactional(readOnly = true)
    public GatewayBlacklist findById(Long id) {
        return gatewayBlacklistService.getGatewayBlacklistById(id);
    }

    @Transactional
    public void save(GatewayBlacklist entry) {
        gatewayBlacklistService.save(entry);
        refreshAfterCommit();
    }

    @Transactional
    public void deleteById(Long id) {
        gatewayBlacklistService.delete(id);
        refreshAfterCommit();
    }

    private void refreshAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                runtimeConfiguration.refreshBlacklist();
            }
        });
    }

}
