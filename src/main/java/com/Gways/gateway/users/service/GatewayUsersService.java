package com.Gways.gateway.users.service;

import com.Gways.gateway.users.entity.GatewayUsers;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface GatewayUsersService extends UserDetailsService {

    List<GatewayUsers> findAll();

    GatewayUsers findById(Long id);

    void save(GatewayUsers user, String rawPassword);

    void deleteById(Long id);
}
