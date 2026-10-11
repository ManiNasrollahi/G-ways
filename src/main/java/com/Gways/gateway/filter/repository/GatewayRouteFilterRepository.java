package com.Gways.gateway.filter.repository;

import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayRouteFilterRepository extends JpaRepository<GatewayRouteFilter, Long> {

    @Query("""
        FROM GatewayRouteFilter f
            join fetch f.route
        where f.id = :filterId
    """)
    Optional<GatewayRouteFilter> findByFilterId(Long filterId);
}
