package com.Gways.gateway.users.repository;

import com.Gways.gateway.users.entity.GatewayUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayUsersRepository extends JpaRepository<GatewayUsers, Long> {

    GatewayUsers findByUsernameAndEnabledTrue(String username);

    GatewayUsers findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
