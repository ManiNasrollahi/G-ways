package com.Gways.controlplane.service;

import com.Gways.configuration.RuntimeConfiguration;
import com.Gways.gateway.cors.entity.GatewayCorsSettings;
import com.Gways.gateway.cors.service.GatewayCorsSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class GatewayCorsAdminService {

    private final GatewayCorsSettingsService corsSettingsService;
    private final RuntimeConfiguration runtimeConfiguration;

    @Transactional(readOnly = true)
    public GatewayCorsSettings getSettings() {
        return corsSettingsService.getSettings();
    }

    @Transactional
    public void save(GatewayCorsSettings settings) {
        corsSettingsService.save(settings);
        refreshAfterCommit();
    }

    private void refreshAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                runtimeConfiguration.refreshCors();
            }
        });
    }
}
