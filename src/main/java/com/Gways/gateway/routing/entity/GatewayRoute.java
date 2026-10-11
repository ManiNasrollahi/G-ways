package com.Gways.gateway.routing.entity;

import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;
import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import com.Gways.gateway.filter.entity.GatewayRouteRateLimit;
import com.Gways.gateway.filter.entity.GatewayRouteRetry;
import com.Gways.gateway.predicate.entity.GatewayRoutePredicate;
import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "gateway_route",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_gateway_route_route_id",
                        columnNames = "route_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "route_id", nullable = false, length = 100)
    private String routeId;

    @Column(name = "route_name", nullable = false, length = 200)
    private String routeName;

    @Column(name = "uri", nullable = false, length = 1000)
    private String uri;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "route_order", nullable = false)
    private Integer routeOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<GatewayRoutePredicate> predicates = new HashSet<>();

    @OneToMany(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<GatewayRouteFilter> filters = new HashSet<>();

    @OneToOne(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private GatewayRouteRateLimit rateLimits;

    @OneToOne(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private GatewayRouteRetry retry;

    @OneToOne(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private GatewayRouteCircuitBreaker circuitBreaker;


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

    public boolean hasFilters(GatewayRouteFilter.FilterPhase phase) {
        return filters.stream().anyMatch(filter -> Boolean.TRUE.equals(filter.getEnabled()) && filter.getFilterType().getPhase() == phase);
    }

    public boolean hasRateLimiter() {
        return rateLimits != null && rateLimits.isEnabled();
    }

    public boolean hasRetry() {
        return retry != null && retry.isEnabled();
    }

    public boolean hasCircuitBreaker() {
        return circuitBreaker != null && circuitBreaker.isEnabled();
    }
}
