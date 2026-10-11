package com.Gways.gateway.filter.service.impl;

import com.Gways.gateway.filter.entity.GatewayRouteRateLimit;
import com.Gways.gateway.filter.repository.GatewayRouteRateLimitRepository;
import com.Gways.gateway.filter.service.GatewayRouteRateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayRouteRateLimitServiceImpl implements GatewayRouteRateLimitService {

    private final GatewayRouteRateLimitRepository gatewayRouteRateLimitRepository;

    @Override
    public GatewayRouteRateLimit findById(Long id) {
        return gatewayRouteRateLimitRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(GatewayRouteRateLimit gatewayRouteRateLimit) {
        gatewayRouteRateLimitRepository.saveAndFlush(gatewayRouteRateLimit);
    }

    @Override
    public void delete(long id) {
        gatewayRouteRateLimitRepository.deleteById(id);
    }
}
