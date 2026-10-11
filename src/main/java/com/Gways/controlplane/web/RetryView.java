package com.Gways.controlplane.web;

import com.Gways.controlplane.service.RouteAdminService;
import com.Gways.controlplane.service.RouteRetryAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.filter.entity.GatewayRouteRetry;
import com.Gways.gateway.routing.entity.GatewayRoute;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Named("retryView")
@ViewScoped
@Getter
@Setter
public class RetryView extends BaseView {

    private final RouteRetryAdminService retryAdminService;
    private final RouteAdminService routeAdminService;

    private List<GatewayRouteRetry> retries;
    private List<GatewayRoute> routes;
    private GatewayRouteRetry retry;

    @Inject
    public RetryView(RouteRetryAdminService retryAdminService, RouteAdminService routeAdminService) {
        this.retryAdminService = retryAdminService;
        this.routeAdminService = routeAdminService;
    }

    @Override
    public void initialize() {
        routes = routeAdminService.findAll();
        retries = new ArrayList<>();
        routes.forEach(route -> {
            if (route.getRetry() != null) retries.add(route.getRetry());
        });
        openNew();
    }

    public long getEnabledCount() {
        return retries.stream().filter(GatewayRouteRetry::isEnabled).count();
    }

    public GatewayRouteRetry.RetryHttpMethod[] getRetryMethods() {
        return GatewayRouteRetry.RetryHttpMethod.values();
    }

    public void openNew() {
        retry = new GatewayRouteRetry();
        retry.setRoute(new GatewayRoute());
    }

    public void openEdit(Long id) {
        retry = retryAdminService.findById(id);
    }

    public void save() {
        try {
            retryAdminService.save(retry);
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Saved", "Retry policy has been saved");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Save error", errorMessageContent(e));
        }
    }

    public void delete(GatewayRouteRetry item) {
        try {
            retryAdminService.delete(item.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Deleted", "Retry policy has been deleted");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Delete error", errorMessageContent(e));
        }
    }
}
