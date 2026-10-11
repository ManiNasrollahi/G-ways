package com.Gways.controlplane.web;

import com.Gways.controlplane.service.RequestAdminService;
import com.Gways.controlplane.service.RouteAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.filter.entity.GatewayRouteFilter;
import com.Gways.gateway.filter.entity.GatewayRouteFilter.FilterTypeEnum;
import com.Gways.gateway.routing.entity.GatewayRoute;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Named("requestManagementView")
@ViewScoped
@Getter
@Setter
public class RequestManagementView extends BaseView {

    private final RequestAdminService requestAdminService;
    private final RouteAdminService routeAdminService;
    private final ObjectMapper mapper;

    private List<GatewayRoute> routes;
    private List<GatewayRouteFilter> filters;

    private List<FilterTypeEnum> SUPPORTED_TYPES = List.of(
            FilterTypeEnum.SET_REQUEST_HEADER, FilterTypeEnum.REMOVE_REQUEST_HEADER,
            FilterTypeEnum.ADD_REQUEST_PARAMETER, FilterTypeEnum.REMOVE_REQUEST_PARAMETER,
            FilterTypeEnum.SET_PATH, FilterTypeEnum.PREFIX_PATH, FilterTypeEnum.STRIP_PREFIX,
            FilterTypeEnum.REQUEST_SIZE, FilterTypeEnum.SET_RESPONSE_HEADER,
            FilterTypeEnum.REMOVE_RESPONSE_HEADER, FilterTypeEnum.SET_STATUS
    );

    private GatewayRouteFilter filter;

    @Inject
    public RequestManagementView(
            RequestAdminService requestAdminService,
            RouteAdminService routeAdminService,
            ObjectMapper mapper
    ) {
        this.requestAdminService = requestAdminService;
        this.routeAdminService = routeAdminService;
        this.mapper = mapper;
    }

    @Override
    public void initialize() {
        routes = routeAdminService.findAll();
        filters = new ArrayList<>();
        openNew();
        routes.forEach(route -> {
            if (route.getFilters() != null)
                filters.addAll(route.getFilters());
        });
    }

    public void openNew() {
        filter = new GatewayRouteFilter();
        filter.setRoute(new GatewayRoute());
    }

    public void openEdit(Long selectedFilterId) {
        this.filter = requestAdminService.findById(selectedFilterId);
        this.filter.convertFromJson();
    }

    public void save() {
        try {
            if (filter == null) {
                message(FacesMessage.SEVERITY_ERROR, "Error", "filter is null");
                return;
            }

            requestAdminService.save(filter);
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Published", "Filter has been saved");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Request management", errorMessageContent(e));;
        }
    }

    public void delete(GatewayRouteFilter filter) {
        try {
            requestAdminService.delete(filter.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Deleted", "Filter has been deleted");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Request management", errorMessageContent(e));
        }
    }

    public boolean isValueRequired(FilterTypeEnum type) {
        return type == FilterTypeEnum.SET_REQUEST_HEADER || type == FilterTypeEnum.ADD_REQUEST_PARAMETER || type == FilterTypeEnum.SET_RESPONSE_HEADER;
    }

    public long getEnabledCount() {
        return filters.stream().filter(f -> Boolean.TRUE.equals(f.getEnabled())).count();
    }

    public String getNameLabel(FilterTypeEnum type) {
        if (type == FilterTypeEnum.SET_STATUS) return "HTTP status code";
        if (type == FilterTypeEnum.REQUEST_SIZE) return "Maximum request size";
        if (type == FilterTypeEnum.STRIP_PREFIX) return "Number of path segments";
        if (type == FilterTypeEnum.SET_PATH) return "Path";
        if (type == FilterTypeEnum.PREFIX_PATH) return "Path prefix";
        return "Name";
    }

    public String getHint(FilterTypeEnum type) {
        if (type == FilterTypeEnum.SET_STATUS) return "Three-digit HTTP status code, for example 200 or 503";
        if (type == FilterTypeEnum.REQUEST_SIZE) return "Size with unit, for example 5MB or 512KB";
        if (type == FilterTypeEnum.SET_PATH) return "For example /api/v1/orders";
        if (type == FilterTypeEnum.PREFIX_PATH) return "For example /api/v1";
        if (type == FilterTypeEnum.STRIP_PREFIX) return "For example 1 to remove the first path segment";
        return "Header or query parameter name";
    }
}
