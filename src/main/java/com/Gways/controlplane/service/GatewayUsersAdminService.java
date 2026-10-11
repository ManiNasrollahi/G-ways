package com.Gways.controlplane.service;

import com.Gways.gateway.users.entity.GatewayUsers;
import com.Gways.gateway.users.service.GatewayUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayUsersAdminService {

    private final GatewayUsersService service;

    @Transactional(readOnly = true)
    public List<GatewayUsers> findAll() {
        return service.findAll();
    }

    @Transactional(readOnly = true)
    public GatewayUsers findById(Long id) {
        return service.findById(id);
    }

    @Transactional
    public void save(GatewayUsers user, String rawPassword) {
        if (user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        service.save(user, rawPassword);
    }

    @Transactional
    public void delete(Long id) {
        service.deleteById(id);
    }
}
