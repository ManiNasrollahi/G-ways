package com.Gways.gateway.filter.repository;

import com.Gways.gateway.filter.entity.GatewayRouteRetry;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayRouteRetryRepository extends JpaRepository<GatewayRouteRetry, Long> {

    @Query("""
        from GatewayRouteRetry r
            join fetch r.route ro
        where r.id = :id
        """)
    @NonNull
    Optional<GatewayRouteRetry> findById(Long id);

    @Query("delete from GatewayRouteRetry where id = :id")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteById(@NonNull Long id);
}
