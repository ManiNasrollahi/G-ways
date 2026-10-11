package com.Gways.gateway.trustedProxies.service.impl;

import com.Gways.gateway.trustedProxies.entity.GatewayTrustedProxy;
import com.Gways.gateway.trustedProxies.repository.TrustedProxiesRepository;
import com.Gways.gateway.trustedProxies.service.GatewayTrustedProxiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayTrustedProxiesServiceImpl implements GatewayTrustedProxiesService {

    private final TrustedProxiesRepository trustedProxiesRepository;

    @Override
    public List<GatewayTrustedProxy> findAllTrustedProxies() {
        return trustedProxiesRepository.findAll();
    }

    @Override
    public GatewayTrustedProxy findById(Long id) {
        return trustedProxiesRepository.findById(id).orElse(null);
    }

    @Override
    public void save(GatewayTrustedProxy gatewayTrustedProxy) {
        trustedProxiesRepository.saveAndFlush(gatewayTrustedProxy);
    }

    @Override
    public void deleteById(Long id) {
        trustedProxiesRepository.deleteById(id);
        trustedProxiesRepository.flush();
    }

}
