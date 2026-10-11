package com.Gways.gateway.blackList.service.impl;

import com.Gways.gateway.blackList.entity.GatewayBlacklist;
import com.Gways.gateway.blackList.repository.GatewayBlacklistRepository;
import com.Gways.gateway.blackList.service.GatewayBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayBlacklistServiceImpl implements GatewayBlacklistService {

    private final GatewayBlacklistRepository gatewayBlacklistRepository;

    @Override
    public List<GatewayBlacklist> getGatewayBlacklist() {
        return gatewayBlacklistRepository.findAllByEnabledTrue();
    }

    @Override
    public List<GatewayBlacklist> findAll() {
        return gatewayBlacklistRepository.findAll();
    }

    @Override
    public GatewayBlacklist getGatewayBlacklistById(long id) {
        return gatewayBlacklistRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(GatewayBlacklist gatewayBlacklist) {
        gatewayBlacklistRepository.saveAndFlush(gatewayBlacklist);
    }

    @Override
    public void delete(long id) {
        gatewayBlacklistRepository.deleteById(id);
        gatewayBlacklistRepository.flush();
    }
}
