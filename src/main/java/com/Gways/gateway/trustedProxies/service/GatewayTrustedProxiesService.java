package com.Gways.gateway.trustedProxies.service;

import com.Gways.gateway.trustedProxies.entity.GatewayTrustedProxy;

import java.util.List;

public interface GatewayTrustedProxiesService {

    List<GatewayTrustedProxy> findAllTrustedProxies();

    GatewayTrustedProxy findById(Long id);

    void save(GatewayTrustedProxy gatewayTrustedProxy);

    void deleteById(Long id);
}
