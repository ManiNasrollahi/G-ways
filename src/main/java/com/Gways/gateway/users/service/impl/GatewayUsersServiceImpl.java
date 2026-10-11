package com.Gways.gateway.users.service.impl;

import com.Gways.gateway.users.repository.GatewayUsersRepository;
import com.Gways.gateway.users.service.GatewayUsersService;
import com.Gways.gateway.users.entity.GatewayUsers;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayUsersServiceImpl implements GatewayUsersService {

    private final GatewayUsersRepository gatewayUsersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        GatewayUsers user = gatewayUsersRepository.findByUsernameAndEnabledTrue(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    @Override
    public List<GatewayUsers> findAll() {
        return gatewayUsersRepository.findAll();
    }

    @Override
    public GatewayUsers findById(Long id) {
        return gatewayUsersRepository.findById(id).orElse(null);
    }


    @Override
    public void save(GatewayUsers user, String rawPassword) {
        user.setUsername(user.getUsername().trim());
        if (user.getId() == null) {
            if (rawPassword == null || rawPassword.isBlank()) {
                throw new IllegalArgumentException("Password is required for a new user");
            }
            if (gatewayUsersRepository.existsByUsernameIgnoreCase(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
        } else if (rawPassword != null && !rawPassword.isBlank()
                && gatewayUsersRepository.findByUsername(user.getUsername()) != null
                && !gatewayUsersRepository.findByUsername(user.getUsername()).getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        gatewayUsersRepository.saveAndFlush(user);
    }

    @Override
    public void deleteById(Long id) {
        gatewayUsersRepository.deleteById(id);
        gatewayUsersRepository.flush();
    }
}
