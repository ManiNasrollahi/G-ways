package com.Gways.gateway.routing.service.impl;

import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.routing.repository.GatewayRouteRepository;
import com.Gways.gateway.routing.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayRouteServiceImpl implements GatewayRouteService {

    private final GatewayRouteRepository gatewayRouteRepository;

    @Override
    public GatewayRoute findRouteById(long id) {
        return gatewayRouteRepository.findRouteById(id).orElse(null);
    }

    @Override
    public List<GatewayRoute> findAllRoutes() {
        return gatewayRouteRepository.findAllRoutes().orElse(new ArrayList<>());
    }

    @Override
    public List<GatewayRoute> findAllByEnabledTrueOrderByRouteOrderAsc() {
        return gatewayRouteRepository.findAllByEnabledTrueOrderByRouteOrderAsc();
    }

    @Override
    public void deleteRouteById(long id) {
        gatewayRouteRepository.deleteById(id);
        gatewayRouteRepository.flush();
    }

    @Override
    public void saveRoute(GatewayRoute route) {
        gatewayRouteRepository.saveAndFlush(route);
    }

    @Override
    public boolean existsRouteById(long id) {
        return gatewayRouteRepository.existsById(id);
    }

}
