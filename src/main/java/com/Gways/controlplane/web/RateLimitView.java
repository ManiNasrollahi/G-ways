package com.Gways.controlplane.web;

import com.Gways.controlplane.service.RouteRateLimitAdminService;
import com.Gways.controlplane.service.RouteAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.filter.entity.GatewayRouteRateLimit;
import com.Gways.gateway.routing.entity.GatewayRoute;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Named("rateLimitView")
@ViewScoped
@Getter
@Setter
public class RateLimitView extends BaseView {

    private final RouteRateLimitAdminService routeRateLimitAdminService;
    private final RouteAdminService routeAdminService;

    private List<GatewayRouteRateLimit> rateLimits;
    private List<GatewayRoute> routes;

    private GatewayRouteRateLimit rateLimit;

    @Inject
    public RateLimitView(
            RouteRateLimitAdminService routeRateLimitAdminService,
            RouteAdminService routeAdminService
    ) {
        this.routeRateLimitAdminService = routeRateLimitAdminService;
        this.routeAdminService = routeAdminService;
    }

    @Override
    public void initialize() {
        routes = routeAdminService.findAll();
        rateLimits = new ArrayList<>();
        routes.forEach(route -> {
            if (route.getRateLimits() != null)
                rateLimits.add(route.getRateLimits());
        });
        openNew();
    }

    public long getEnabledCount() {
        return rateLimits.stream().filter(GatewayRouteRateLimit::isEnabled).count();
    }

    public void openNew() {
        rateLimit = new GatewayRouteRateLimit();
        rateLimit.setRoute(new GatewayRoute());
    }

    public void openEdit(Long id) {
        rateLimit = routeRateLimitAdminService.findById(id);
    }

    public void save() {
        try {
            routeRateLimitAdminService.save(rateLimit);
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Saved", "Rate limit has been saved");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Save error", errorMessageContent(e));
        }
    }

    public void delete(GatewayRouteRateLimit item) {
        try {
            routeRateLimitAdminService.delete(item.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Deleted", "Rate limit has been deleted");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Delete error", errorMessageContent(e));
        }
    }
}
