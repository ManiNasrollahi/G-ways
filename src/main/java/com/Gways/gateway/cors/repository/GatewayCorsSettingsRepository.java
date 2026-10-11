package com.Gways.gateway.cors.repository;

import com.Gways.gateway.cors.entity.GatewayCorsSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayCorsSettingsRepository extends JpaRepository<GatewayCorsSettings, Long> {

    Optional<GatewayCorsSettings> findTopByOrderByIdAsc();
}
