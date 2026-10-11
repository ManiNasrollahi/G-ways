package com.Gways.gateway.filter.service.impl;

import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;
import com.Gways.gateway.filter.repository.GatewayRouteCircuitBreakerRepository;
import com.Gways.gateway.filter.service.GatewayRouteCircuitBreakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayRouteCircuitBreakerServiceImpl implements GatewayRouteCircuitBreakerService {

    private final GatewayRouteCircuitBreakerRepository gatewayRouteCircuitBreakerRepository;

    @Override
    public GatewayRouteCircuitBreaker findById(Long id) {
        return gatewayRouteCircuitBreakerRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(GatewayRouteCircuitBreaker circuitBreaker) {
        gatewayRouteCircuitBreakerRepository.saveAndFlush(circuitBreaker);
    }

    @Override
    public void delete(long id) {
        gatewayRouteCircuitBreakerRepository.deleteById(id);
    }

}
