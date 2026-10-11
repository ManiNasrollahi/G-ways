package com.Gways.gateway.filter.entity;

import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "gateway_route_retry")
@Getter
@Setter
@NoArgsConstructor
public class GatewayRouteRetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "route_id",
            nullable = false,
            unique = true)
    private GatewayRoute route;

    @Convert(converter = HttpMethodsConverter.class)
    @Column(name = "http_method", nullable = false, length = 100)
    private Set<RetryHttpMethod> methods = new HashSet<>();

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "first_backoff_S", nullable = false)
    private Long firstBackoffSeconds;

    @Column(name = "max_backoff_S", nullable = false)
    private Long maxBackoffSeconds;

    @Column(name = "backoff_factor", nullable = false)
    private Integer backoffFactor;

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

    public enum RetryHttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        OPTIONS,
        HEAD;

        public HttpMethod toHttpMethod() {
            return HttpMethod.valueOf(name());
        }
    }
}
