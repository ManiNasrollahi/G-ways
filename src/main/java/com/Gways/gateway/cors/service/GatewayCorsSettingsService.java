package com.Gways.gateway.cors.service;

import com.Gways.gateway.cors.entity.GatewayCorsSettings;

public interface GatewayCorsSettingsService {

    GatewayCorsSettings getSettings();

    void save(GatewayCorsSettings settings);
}
