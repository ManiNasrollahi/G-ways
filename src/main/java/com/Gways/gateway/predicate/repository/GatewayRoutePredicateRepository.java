package com.Gways.gateway.predicate.repository;

import com.Gways.gateway.predicate.entity.GatewayRoutePredicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRoutePredicateRepository extends JpaRepository<GatewayRoutePredicate, Long> {

}
