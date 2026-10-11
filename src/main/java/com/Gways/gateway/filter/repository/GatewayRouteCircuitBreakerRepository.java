package com.Gways.gateway.filter.repository;

import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface GatewayRouteCircuitBreakerRepository extends JpaRepository<GatewayRouteCircuitBreaker, Long> {

    @Query("delete from GatewayRouteCircuitBreaker where id = :id")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteById(long id);
}
