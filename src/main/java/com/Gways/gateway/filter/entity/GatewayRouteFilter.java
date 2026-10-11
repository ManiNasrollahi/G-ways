package com.Gways.gateway.filter.entity;

import com.Gways.gateway.dto.*;
import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "gateway_route_filter")
@Getter
@Setter
public class GatewayRouteFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private GatewayRoute route;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "filter_type",
            nullable = false,
            length = 50
    )
    private FilterTypeEnum filterType;

    @Column(name = "filter_config", length = 2500, nullable = false)
    private String filterConfig;

    @Column(nullable = false)
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


    /*
     * ============================
     * UI Fields
     * ============================
     */


    @Transient
    private String name;

    @Transient
    private String value;

    @Getter
    public enum FilterTypeEnum {
        SET_REQUEST_HEADER(FilterPhase.BEFORE),
        REMOVE_REQUEST_HEADER(FilterPhase.BEFORE),
        ADD_REQUEST_PARAMETER(FilterPhase.BEFORE),
        REMOVE_REQUEST_PARAMETER(FilterPhase.BEFORE),
        SET_PATH(FilterPhase.BEFORE),
        PREFIX_PATH(FilterPhase.BEFORE),
        STRIP_PREFIX(FilterPhase.BEFORE),
        SET_RESPONSE_HEADER(FilterPhase.AFTER),
        REMOVE_RESPONSE_HEADER(FilterPhase.AFTER),
        REQUEST_SIZE(FilterPhase.BEFORE),
        SET_STATUS(FilterPhase.AFTER);

        private final FilterPhase phase;

        FilterTypeEnum(FilterPhase phase) {
            this.phase = phase;
        }
    }

    public enum FilterPhase {
        BEFORE,
        AFTER
    }

    public void convertToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            Object config = switch (filterType) {
                case SET_STATUS, REMOVE_RESPONSE_HEADER, REMOVE_REQUEST_HEADER,
                     REMOVE_REQUEST_PARAMETER, REQUEST_SIZE, SET_PATH,
                     PREFIX_PATH, STRIP_PREFIX -> new NameDto(name);
                case SET_REQUEST_HEADER, ADD_REQUEST_PARAMETER, SET_RESPONSE_HEADER -> new NameValueDto(name, value);
            };

            this.filterConfig = objectMapper.writeValueAsString(config);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot convert predicate configuration to JSON",
                    e
            );
        }
    }

    public void convertFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        if (filterConfig == null || filterConfig.isBlank()) {
            return;
        }

        try {
            switch (filterType) {

                case SET_STATUS, REMOVE_RESPONSE_HEADER, REMOVE_REQUEST_HEADER,
                     REMOVE_REQUEST_PARAMETER, REQUEST_SIZE, SET_PATH,
                     PREFIX_PATH, STRIP_PREFIX -> {
                        NameDto config = objectMapper.readValue(filterConfig, NameDto.class);
                        this.name = config.name();
                }

                case SET_REQUEST_HEADER, ADD_REQUEST_PARAMETER, SET_RESPONSE_HEADER -> {
                    NameValueDto config = objectMapper.readValue(filterConfig, NameValueDto.class);
                    this.name = config.name();
                    this.value = config.value();
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot convert predicate JSON to UI fields",
                    e
            );
        }
    }
}
