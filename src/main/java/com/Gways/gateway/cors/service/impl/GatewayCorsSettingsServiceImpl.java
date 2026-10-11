package com.Gways.gateway.cors.service.impl;

import com.Gways.gateway.cors.entity.GatewayCorsSettings;
import com.Gways.gateway.cors.repository.GatewayCorsSettingsRepository;
import com.Gways.gateway.cors.service.GatewayCorsSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayCorsSettingsServiceImpl implements GatewayCorsSettingsService {

    private final GatewayCorsSettingsRepository repository;

    @Override
    public GatewayCorsSettings getSettings() {
        return repository.findTopByOrderByIdAsc().orElseGet(GatewayCorsSettings::new);
    }

    @Override
    public void save(GatewayCorsSettings settings) {
        validate(settings);
        repository.saveAndFlush(settings);
    }

    private void validate(GatewayCorsSettings settings) {
        if (settings.getMaxAge() < 0) {
            throw new IllegalArgumentException("Max age cannot be negative");
        }

        if (settings.getAllowedOrigins() == null || settings.getAllowedOrigins().isEmpty()) {
            throw new IllegalArgumentException("At least one allowed origin is required when CORS is enabled");
        }

    }
}
