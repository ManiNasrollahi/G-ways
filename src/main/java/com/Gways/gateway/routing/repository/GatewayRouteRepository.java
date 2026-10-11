package com.Gways.gateway.routing.repository;

import com.Gways.gateway.routing.entity.GatewayRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GatewayRouteRepository extends JpaRepository<GatewayRoute, Long> {

    @Query(value = """
        FROM GatewayRoute g
            left join fetch g.predicates p
            left join fetch g.filters f
            left join fetch g.rateLimits r
            left join fetch g.retry re
        where g.id = :id
        """)
    Optional<GatewayRoute> findRouteById(long id);

    @Query(value = """
        FROM GatewayRoute g
            left join fetch g.predicates p
            left join fetch g.filters f
            left join fetch g.rateLimits r
            left join fetch g.retry re
            left join fetch g.circuitBreaker cb
    """)
    Optional<List<GatewayRoute>> findAllRoutes();

    @Query(value = """
        FROM GatewayRoute g
            left join fetch g.predicates p
            left join fetch g.filters f
            left join fetch g.rateLimits r
            left join fetch g.retry re
            left join fetch g.circuitBreaker cb
        where g.enabled = true
        order by g.routeOrder asc
    """)
    List<GatewayRoute> findAllByEnabledTrueOrderByRouteOrderAsc();

}
