package com.Gways.gateway.blackList.repository;

import com.Gways.gateway.blackList.entity.GatewayBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GatewayBlacklistRepository extends JpaRepository<GatewayBlacklist, Long> {

    List<GatewayBlacklist> findAllByEnabledTrue();
}
