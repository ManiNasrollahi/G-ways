package com.Gways.gateway.filter.service.impl;

import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import com.Gways.gateway.filter.repository.GatewayRouteFilterRepository;
import com.Gways.gateway.filter.service.GatewayRouteFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayRouteFilterServiceImpl implements GatewayRouteFilterService {

    private final GatewayRouteFilterRepository gatewayRouteFilterRepository;

    @Override
    public GatewayRouteFilter findByFilterId(Long filterId) {
        return gatewayRouteFilterRepository.findByFilterId(filterId).orElse(null);
    }

    @Override
    public void save(GatewayRouteFilter gatewayRouteFilter) {
        gatewayRouteFilterRepository.saveAndFlush(gatewayRouteFilter);
    }

    @Override
    public void deleteById(Long filterId) {
        gatewayRouteFilterRepository.deleteById(filterId);
        gatewayRouteFilterRepository.flush();
    }
}
