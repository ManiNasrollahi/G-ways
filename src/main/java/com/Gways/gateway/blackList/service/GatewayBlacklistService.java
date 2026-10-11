package com.Gways.gateway.blackList.service;

import com.Gways.gateway.blackList.entity.GatewayBlacklist;

import java.util.List;

public interface GatewayBlacklistService {

    List<GatewayBlacklist> getGatewayBlacklist();

    List<GatewayBlacklist> findAll();

    GatewayBlacklist getGatewayBlacklistById(long id);

    void save(GatewayBlacklist gatewayBlacklist);

    void delete(long id);
}
