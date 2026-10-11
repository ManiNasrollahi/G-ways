package com.Gways.gateway.trustedProxies.repository;

import com.Gways.gateway.trustedProxies.entity.GatewayTrustedProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrustedProxiesRepository extends JpaRepository<GatewayTrustedProxy, Long> {

    List<GatewayTrustedProxy> findAllByEnabledTrue();
}
