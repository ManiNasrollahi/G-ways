package com.Gways.gateway.filter.repository;

import com.Gways.gateway.filter.entity.GatewayRouteRateLimit;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayRouteRateLimitRepository extends JpaRepository<GatewayRouteRateLimit, Long> {

    @Query("""
        from GatewayRouteRateLimit g
            join fetch g.route r
        where g.id = :id
    """)
    @NonNull
    Optional<GatewayRouteRateLimit> findById(Long id);

    @Query("delete from GatewayRouteRateLimit where id = :id")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteById(long id);
}
