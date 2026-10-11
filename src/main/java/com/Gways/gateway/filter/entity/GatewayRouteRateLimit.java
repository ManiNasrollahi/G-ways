package com.Gways.gateway.filter.entity;

import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "gateway_route_rate_limit",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_gateway_route_rate_limit_route",
                        columnNames = "route_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRouteRateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "route_id",
            nullable = false,
            unique = true
    )
    private GatewayRoute route;

    @Column(name = "period", nullable = false)
    private long period;

    @Column(name = "capacity", nullable = false)
    private long capacity;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

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
