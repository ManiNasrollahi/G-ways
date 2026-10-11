package com.Gways.gateway.filter.entity;

import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "gateway_route_circuit_breaker",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_gateway_route_circuit_breaker_route",
                        columnNames = "route_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRouteCircuitBreaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "route_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_gateway_route_circuit_breaker_route"
            )
    )
    private GatewayRoute route;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "minimum_number_of_calls", nullable = false)
    private int minimumNumberOfCalls;

    @Column(name = "sliding_window_size", nullable = false)
    private int slidingWindowSize;

    @Column(name = "failure_rate_threshold", nullable = false)
    private float failureRateThreshold;

    @Column(name = "wait_duration_in_open_state_seconds", nullable = false)
    private long waitDurationInOpenStateSeconds;

    @Column(name = "permitted_number_of_calls_in_half_open_state", nullable = false)
    private int permittedNumberOfCallsInHalfOpenState;

    @Column(name = "timeOut_in_seconds", nullable = false)
    private int timeOutInSeconds;

    @Convert(converter = HttpStatusConverter.class)
    @Column(name = "http_status", nullable = false, length = 500)
    private List<Integer> httpStatusList;

    @Column(name = "fall_back_uri", length = 500)
    private String fallBackUri;

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
