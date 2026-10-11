package com.Gways.gateway.predicate.entity;


import com.Gways.gateway.dto.BetweenDto;
import com.Gways.gateway.dto.NameValueDto;
import com.Gways.gateway.dto.PatternsDto;
import com.Gways.gateway.dto.TimeDto;
import com.Gways.gateway.routing.entity.GatewayRoute;
import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "gateway_route_predicate",
        indexes = {
                @Index(
                        name = "ix_gateway_route_predicate_route",
                        columnList = "route_id, enabled"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRoutePredicate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "route_id",
            nullable = false
    )
    private GatewayRoute route;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "predicate_type",
            nullable = false,
            length = 50
    )
    private PredicateTypeEnum predicateType;

    @Column(
            name = "predicate_config",
            nullable = false,
            length = 2500
    )
    private String predicateConfig;

    @Column(
            name = "enabled",
            nullable = false
    )
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
    private List<String> patterns;

    @Transient
    private String name;

    @Transient
    private String value;

    @Transient
    private ZonedDateTime dateTime1;

    @Transient
    private ZonedDateTime dateTime2;

    @Transient
    private ZonedDateTime dateTime;


    public GatewayRoutePredicate(GatewayRoute route) {
        this.route = route;
    }


    public enum PredicateTypeEnum {
        PATH,
        METHOD,
        HEADER,
        HOST,
        QUERY,
        COOKIE,
        BEFORE,
        AFTER,
        BETWEEN
    }

    public void convertToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            Object config = switch (predicateType) {
                case PATH, METHOD, HOST -> new PatternsDto(patterns);
                case HEADER, QUERY, COOKIE -> new NameValueDto(name, value);
                case BEFORE, AFTER -> new TimeDto(dateTime);
                case BETWEEN -> new BetweenDto(dateTime1, dateTime2);
            };

            this.predicateConfig = objectMapper.writeValueAsString(config);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot convert predicate configuration to JSON",
                    e
            );
        }
    }

    public void convertFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        if (predicateConfig == null || predicateConfig.isBlank()) {
            return;
        }

        try {
            switch (predicateType) {
                case PATH, METHOD, HOST -> {
                    PatternsDto config =objectMapper.readValue(predicateConfig, PatternsDto.class);
                    this.patterns = config.patterns();
                }

                case HEADER, QUERY, COOKIE -> {
                    NameValueDto config = objectMapper.readValue(predicateConfig, NameValueDto.class);
                    this.name = config.name();
                    this.value = config.value();
                }

                case BEFORE, AFTER -> {
                    TimeDto config = objectMapper.readValue(predicateConfig, TimeDto.class);
                    this.dateTime = config.dateTime();
                }

                case BETWEEN -> {
                    BetweenDto config = objectMapper.readValue(predicateConfig, BetweenDto.class);
                    this.dateTime1 = config.dateTime1();
                    this.dateTime2 = config.dateTime2();
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