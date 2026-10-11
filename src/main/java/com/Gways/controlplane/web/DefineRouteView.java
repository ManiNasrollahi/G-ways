package com.Gways.controlplane.web;

import com.Gways.controlplane.service.RouteAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.predicate.entity.GatewayRoutePredicate;
import com.Gways.gateway.routing.entity.GatewayRoute;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;

@Named("defineRouteView")
@ViewScoped
@Getter
@Setter
public class DefineRouteView extends BaseView {


    private final RouteAdminService routeAdminService;

    private List<GatewayRoute> routes;
    private GatewayRoute route;

    @Inject
    public DefineRouteView(
            RouteAdminService routeAdminService
    ) {
        this.routeAdminService = routeAdminService;
    }

    @Override
    public void initialize() {
        routes = routeAdminService.findAll();
    }

    public long getEnabledCount() {
        return routes.stream().filter(GatewayRoute::getEnabled).count();
    }

    public void openNew() {
        this.route = new GatewayRoute();
        this.route.setEnabled(true);
        this.route.setPredicates(new HashSet<>());
    }

    public void openEdit(Long routeId) {
        this.route = routeAdminService.findById(routeId);
        this.route.getPredicates()
                .forEach(GatewayRoutePredicate::convertFromJson);
    }

    public void addPredicate() {
        if (route.getPredicates() == null) {
            route.setPredicates(new HashSet<>());
        }
        route.getPredicates().add(new GatewayRoutePredicate(route));
    }

    public void removePredicate(GatewayRoutePredicate index) {
        route.getPredicates().remove(index);
    }


    public void save() {
        try {
            if (route == null) {
                message(FacesMessage.SEVERITY_ERROR, "Error", "route is null");
                return;
            }
            if (route.getPredicates() == null || route.getPredicates().isEmpty()) {
                message(FacesMessage.SEVERITY_ERROR, "Error", "route predicates is empty");
                return;
            }
            routeAdminService.save(route);
            message(FacesMessage.SEVERITY_INFO, "Info", "Route has been saved");
            initialize();
        } catch (Exception exception) {
            message(FacesMessage.SEVERITY_ERROR, "save error", errorMessageContent(exception));
        }
    }

    public void delete(GatewayRoute route) {
        try {
            routeAdminService.deleteRouteById(route.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "deleted", "route has been deleted");
        } catch (Exception exception) {
            message(FacesMessage.SEVERITY_ERROR, "error in delete", errorMessageContent(exception));
        }
    }

    public GatewayRoutePredicate.PredicateTypeEnum[] getPredicateTypes() {
        return GatewayRoutePredicate.PredicateTypeEnum.values();
    }



}
