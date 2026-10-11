package com.Gways.configuration;

import com.Gways.gateway.blackList.entity.GatewayBlacklist;
import com.Gways.gateway.blackList.service.GatewayBlacklistService;
import com.Gways.gateway.cors.entity.GatewayCorsSettings;
import com.Gways.gateway.cors.service.GatewayCorsSettingsService;
import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.routing.service.GatewayRouteService;
import com.Gways.gateway.trustedProxies.entity.GatewayTrustedProxy;
import com.Gways.gateway.trustedProxies.service.GatewayTrustedProxiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;


import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RuntimeConfiguration {

    private final GatewayRouteService routeService;
    private final GatewayTrustedProxiesService gatewayTrustedProxiesService;
    private final GatewayBlacklistService gatewayBlacklistService;
    private final GatewayCorsSettingsService corsSettingsService;

    private final AtomicReference<List<GatewayRoute>> routeSnapshot = new AtomicReference<>(List.of());
    private final AtomicReference<List<String>> trustedProxiesSnapshot = new AtomicReference<>(List.of());
    private final AtomicReference<List<String>> blacklistSnapshot = new AtomicReference<>(List.of());
    private final AtomicReference<CorsConfiguration> corsSnapshot = new AtomicReference<>(new CorsConfiguration());

    public void refreshRouteSnapshot() {
        routeSnapshot.set(routeService.findAllByEnabledTrueOrderByRouteOrderAsc());
    }

    public List<GatewayRoute> getRouteSnapshot() {
        return routeSnapshot.get();
    }

    public void refreshTrustedProxies() {
        trustedProxiesSnapshot.set(
                gatewayTrustedProxiesService
                        .findAllTrustedProxies()
                        .stream()
                        .map(GatewayTrustedProxy::getPattern)
                        .toList()
        );
    }

    public String getTrustedProxiesRegex() {
        return trustedProxiesSnapshot.get().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining("|"));
    }

    public void refreshBlacklist() {
        blacklistSnapshot.set(
                gatewayBlacklistService.getGatewayBlacklist()
                        .stream()
                        .map(GatewayBlacklist::getIp)
                        .toList()
        );
    }

    public List<String> getBlacklist() {
        return blacklistSnapshot.get();
    }

    public void refreshCors() {
        GatewayCorsSettings settings = corsSettingsService.getSettings();
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(settings.getAllowedOrigins());
        configuration.setAllowedMethods(settings.getAllowedMethods());
        configuration.setAllowedHeaders(settings.getAllowedHeaders());
        configuration.setExposedHeaders(settings.getExposedHeaders());
        configuration.setAllowCredentials(settings.isAllowCredentials());
        configuration.setMaxAge(settings.getMaxAge());

        corsSnapshot.set(configuration);
    }

    public CorsConfiguration getCorsConfiguration() {
        return corsSnapshot.get();
    }

}
