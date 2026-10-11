package com.Gways.gateway.cors.entity;

import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Entity
@Table(name = "gateway_cors_settings")
@Getter
@Setter
public class GatewayCorsSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allowed_origins", nullable = false, length = 4000)
    @Convert(converter = CorsConfigConverter.class)
    private List<String> allowedOrigins = new ArrayList<>();

    @Column(name = "allowed_methods", nullable = false, length = 1000)
    @Convert(converter = CorsConfigConverter.class)
    private List<String> allowedMethods = new ArrayList<>(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));

    @Column(name = "allowed_headers", nullable = false, length = 4000)
    @Convert(converter = CorsConfigConverter.class)
    private List<String> allowedHeaders = new ArrayList<>(Arrays.asList(
            "Authorization", "Content-Type", "X-Request-ID"
    ));

    @Column(name = "exposed_headers", nullable = false, length = 4000)
    @Convert(converter = CorsConfigConverter.class)
    private List<String> exposedHeaders = new ArrayList<>();

    @Column(name = "allow_credentials", nullable = false)
    private boolean allowCredentials = false;

    @Column(name = "max_age", nullable = false)
    private long maxAge = 3600;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "register_user", nullable = false, length = 500)
    private String registerUser;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            registerUser = ((GatewayUsers) Objects.requireNonNull(authentication.getPrincipal())).getUsername();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            registerUser = ((GatewayUsers) Objects.requireNonNull(authentication.getPrincipal())).getUsername();
        }
    }
}
