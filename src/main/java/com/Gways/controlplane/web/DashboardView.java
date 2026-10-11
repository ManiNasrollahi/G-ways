package com.Gways.controlplane.web;

import com.Gways.controlplane.service.RouteAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.dto.ServerStatus;
import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import com.Gways.gateway.predicate.entity.GatewayRoutePredicate;
import com.Gways.gateway.routing.entity.GatewayRoute;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Named("dashboardView")
@ViewScoped
@Getter
public class DashboardView extends BaseView {

    private final RouteAdminService routeAdminService;
    private List<RouteReport> routes = List.of();
    private LocalDateTime refreshedAt;

    @Inject
    public DashboardView(RouteAdminService routeAdminService) {
        this.routeAdminService = routeAdminService;
    }

    @Override
    public void initialize() {
        refresh();
    }

    public void refresh() {
        try {
            var savedRoutes = routeAdminService.findAll();
            var serverStatuses = routeAdminService.checkServers(savedRoutes);
            routes = savedRoutes.stream()
                    .sorted(Comparator.comparing(GatewayRoute::getRouteOrder).thenComparing(GatewayRoute::getRouteId))
                    .map(route -> {
                        var report = report(route);
                        report.serverStatus = serverStatuses.get(route.getUri());
                        return report;
                    }).toList();
            refreshedAt = LocalDateTime.now();
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Report unavailable", errorMessageContent(e));
        }
    }

    public long getEnabledCount() {
        return routes.stream().filter(RouteReport::isEnabled).count();
    }
    public long getFilterCount() {
        return routes.stream().flatMap(r -> r.getRules().stream()).filter(r -> !r.getGroup().equals("Predicate")).count();
    }
    public long getWarningCount() {
        return routes.stream().filter(r -> !r.getWarning().isEmpty()).count();
    }

    private RouteReport report(GatewayRoute route) {
        List<RuleReport> rules = new ArrayList<>();

        route.getPredicates().stream().sorted(Comparator.comparingLong(GatewayRoutePredicate::getId))
                .forEach(p ->
                        rules.add(
                                new RuleReport(
                                        "Predicate",
                                        String.valueOf(p.getPredicateType()),
                                        Boolean.TRUE.equals(p.getEnabled()),
                                        p.getPredicateConfig()
                                )
                        )
                );

        route.getFilters().stream().sorted(Comparator.comparingLong(GatewayRouteFilter::getId))
                .forEach(f ->
                        rules.add(
                                new RuleReport(
                                        f.getFilterType().getPhase().name(),
                                        f.getFilterType().name(),
                                        Boolean.TRUE.equals(f.getEnabled()),
                                        f.getFilterConfig()
                                )
                        )
                );

        var limit = route.getRateLimits();
        if (limit != null)
            rules.add(
                    new RuleReport(
                            "Policy",
                            "RATE_LIMIT",
                            limit.isEnabled(),
                            "Capacity: " + limit.getCapacity() + " requests / " + limit.getPeriod() + " seconds per client IP"
                    )
            );

        var retry = route.getRetry();
        if (retry != null)
            rules.add(
                    new RuleReport(
                            "Policy",
                            "RETRY", retry.isEnabled(),
                            "Retries: " + retry.getRetryCount() +
                                    "; methods: " + retry.getMethods() +
                                    "; backoff: " + retry.getFirstBackoffSeconds() + "–" + retry.getMaxBackoffSeconds() + " seconds;" +
                                    " multiplier: " + retry.getBackoffFactor()
                    )
            );

        var cb = route.getCircuitBreaker();
        if (cb != null)
            rules.add(
                    new RuleReport(
                            "Policy",
                            "CIRCUIT_BREAKER",
                            cb.isEnabled(),
                "Minimum calls: " + cb.getMinimumNumberOfCalls() +
                        "; window: " + cb.getSlidingWindowSize() +
                        "; failure threshold: " + cb.getFailureRateThreshold() + "%; open wait: " + cb.getWaitDurationInOpenStateSeconds() + " seconds; half-open calls: " + cb.getPermittedNumberOfCallsInHalfOpenState()
                        + "; timeout: " + cb.getTimeOutInSeconds() + " seconds" +
                        " ; failure statuses: " + cb.getHttpStatusList()
                        + "; fallback: " + cb.getFallBackUri()
                    )
            );

        String warning = route.getPredicates().stream().noneMatch(p -> Boolean.TRUE.equals(p.getEnabled()))
                ? "No enabled predicate. Review this route before publishing." : "";

        return new RouteReport(
                route.getRouteName(),
                route.getRouteId(),
                route.getUri(),
                route.getRouteOrder(),
                Boolean.TRUE.equals(route.getEnabled()),
                warning,
                rules
        );
    }

    @Getter
    public static class RouteReport implements Serializable {

        private final String name, routeId, uri, warning;
        private final int priority;
        private final boolean enabled;
        private final List<RuleReport> rules;
        private ServerStatus serverStatus;

        public RouteReport(String name, String routeId, String uri, int priority, boolean enabled, String warning, List<RuleReport> rules) {
            this.name = name; this.routeId = routeId; this.uri = uri; this.priority = priority;
            this.enabled = enabled; this.warning = warning; this.rules = List.copyOf(rules);
        }
    }

    @Getter
    public static class RuleReport implements Serializable {

        private final String group, type, configuration;
        private final boolean enabled;
        public RuleReport(String group, String type, boolean enabled, String configuration) {
            this.group = group; this.type = type; this.enabled = enabled; this.configuration = configuration;
        }

    }
}
