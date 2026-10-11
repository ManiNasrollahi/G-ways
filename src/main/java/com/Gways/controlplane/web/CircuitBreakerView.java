package com.Gways.controlplane.web;

import com.Gways.controlplane.service.RouteAdminService;
import com.Gways.controlplane.service.RouteCircuitBreakerAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.filter.entity.GatewayRouteCircuitBreaker;
import com.Gways.gateway.routing.entity.GatewayRoute;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named("circuitBreakerView")
@ViewScoped
@Getter
@Setter
public class CircuitBreakerView extends BaseView {

    private final RouteCircuitBreakerAdminService circuitBreakerAdminService;
    private final RouteAdminService routeAdminService;

    private List<GatewayRouteCircuitBreaker> circuitBreakers;
    private List<GatewayRoute> routes;
    private GatewayRouteCircuitBreaker circuitBreaker;

    @Inject
    public CircuitBreakerView(
            RouteCircuitBreakerAdminService circuitBreakerAdminService,
            RouteAdminService routeAdminService
    ) {
        this.circuitBreakerAdminService = circuitBreakerAdminService;
        this.routeAdminService = routeAdminService;
    }

    @Override
    public void initialize() {
        routes = routeAdminService.findAll();
        circuitBreakers = new ArrayList<>();
        routes.forEach(route -> {
            if (route.getCircuitBreaker() != null) {
                circuitBreakers.add(route.getCircuitBreaker());
            }
        });
        openNew();
    }

    public long getEnabledCount() {
        return circuitBreakers.stream().filter(GatewayRouteCircuitBreaker::isEnabled).count();
    }

    public List<HttpStatus> getHttpStatuses() {
        return Arrays.stream(HttpStatus.values()).collect(Collectors.toList());
    }

    public void openNew() {
        circuitBreaker = new GatewayRouteCircuitBreaker();
        circuitBreaker.setRoute(new GatewayRoute());
        circuitBreaker.setEnabled(true);
    }

    public void openEdit(Long id) {
        circuitBreaker = circuitBreakerAdminService.findById(id);
    }

    public void save() {
        try {
            circuitBreakerAdminService.save(circuitBreaker);
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Saved", "Circuit breaker has been saved");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Save error", errorMessageContent(e));
        }
    }

    public void delete(GatewayRouteCircuitBreaker item) {
        try {
            circuitBreakerAdminService.delete(item.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Deleted", "Circuit breaker has been deleted");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Delete error", errorMessageContent(e));
        }
    }
}
