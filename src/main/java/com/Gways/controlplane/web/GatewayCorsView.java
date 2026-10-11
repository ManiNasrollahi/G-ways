package com.Gways.controlplane.web;

import com.Gways.controlplane.service.GatewayCorsAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.cors.entity.GatewayCorsSettings;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named("gatewayCorsView")
@ViewScoped
@Getter
@Setter
public class GatewayCorsView extends BaseView {

    private final GatewayCorsAdminService adminService;
    private GatewayCorsSettings settings;

    @Inject
    public GatewayCorsView(GatewayCorsAdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void initialize() {
        settings = adminService.getSettings();
    }

    public void save() {
        try {
            adminService.save(settings);
            message(FacesMessage.SEVERITY_INFO, "Saved", "Global CORS settings have been published");
        } catch (Exception exception) {
            message(FacesMessage.SEVERITY_ERROR, "Save error", errorMessageContent(exception));
        }
    }
}
