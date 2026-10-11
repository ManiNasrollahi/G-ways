package com.Gways.gateway.trustedProxies.entity;

import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "gateway_trusted_proxy")
@Getter
@Setter
public class GatewayTrustedProxy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pattern", nullable = false, length = 255)
    private String pattern;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

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
