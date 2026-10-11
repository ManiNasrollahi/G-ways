package com.Gways.controlplane.web;

import com.Gways.controlplane.service.TrustedProxiesAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.trustedProxies.entity.GatewayTrustedProxy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Named("trustedProxiesView")
@ViewScoped
@Getter
@Setter
public class TrustedProxiesView extends BaseView {

    private final TrustedProxiesAdminService trustedProxiesAdminService;

    private List<GatewayTrustedProxy> gatewayTrustedProxies;
    private GatewayTrustedProxy trustedProxy;

    @Inject
    public TrustedProxiesView(
            TrustedProxiesAdminService trustedProxiesAdminService
    ) {
        this.trustedProxiesAdminService = trustedProxiesAdminService;
    }

    @Override
    public void initialize() {
        gatewayTrustedProxies = trustedProxiesAdminService.findAllTrustedProxies();
    }

    public long getEnabledCount() {
        return gatewayTrustedProxies.stream().filter(GatewayTrustedProxy::getEnabled).count();
    }

    public void openNew() {
        trustedProxy = new GatewayTrustedProxy(); trustedProxy.setEnabled(true);
    }

    public void openEdit(Long id) {
        trustedProxy = trustedProxiesAdminService.findById(id);
    }

    public void save() {
        try {
            trustedProxiesAdminService.save(trustedProxy);
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Saved", "Trusted proxy has been saved");
        }
        catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Save error", errorMessageContent(e));
        }
    }
    public void delete(GatewayTrustedProxy proxy) {
        try {
            trustedProxiesAdminService.deleteById(proxy.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Deleted", "Trusted proxy has been deleted");
        }
        catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Delete error", errorMessageContent(e));
        }
    }
}
