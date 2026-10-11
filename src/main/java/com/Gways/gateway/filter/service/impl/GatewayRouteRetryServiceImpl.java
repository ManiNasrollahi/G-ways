package com.Gways.gateway.filter.service.impl;

import com.Gways.gateway.filter.repository.GatewayRouteRetryRepository;
import com.Gways.gateway.filter.entity.GatewayRouteRetry;
import com.Gways.gateway.filter.service.GatewayRouteRetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayRouteRetryServiceImpl implements GatewayRouteRetryService {

    private final GatewayRouteRetryRepository gatewayRouteRetryRepository;

    @Override
    public GatewayRouteRetry findById(Long id) {
        return gatewayRouteRetryRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(GatewayRouteRetry retry) {
        gatewayRouteRetryRepository.saveAndFlush(retry);
    }

    @Override
    public void delete(long id) {
        gatewayRouteRetryRepository.deleteById(id);
        gatewayRouteRetryRepository.flush();
    }
}
