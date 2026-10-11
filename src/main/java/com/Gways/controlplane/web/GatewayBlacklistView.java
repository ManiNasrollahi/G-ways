package com.Gways.controlplane.web;

import com.Gways.controlplane.service.BlacklistAdminService;
import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.blackList.entity.GatewayBlacklist;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Named("gatewayBlacklistView")
@ViewScoped
@Getter @Setter
public class GatewayBlacklistView extends BaseView {

    private final BlacklistAdminService adminService;

    private List<GatewayBlacklist> entries;
    private GatewayBlacklist entry;

    @Inject
    public GatewayBlacklistView(
            BlacklistAdminService adminService
    ) {
        this.adminService = adminService;
    }

    @Override public void initialize() {
        entries = adminService.findAll();
    }

    public long getEnabledCount() {
        return entries.stream().filter(GatewayBlacklist::isEnabled).count();
    }

    public void openNew() {
        entry = GatewayBlacklist.builder().enabled(true).build();
    }

    public void openEdit(Long id) {
        entry = adminService.findById(id);
    }

    public void save() {
        try {
            adminService.save(entry);
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Saved", "Blacklist entry has been saved");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Save error", errorMessageContent(e));
        }
    }

    public void delete(GatewayBlacklist item) {
        try {
            adminService.deleteById(item.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Deleted", "Blacklist entry has been deleted");
        } catch (Exception e) {
            message(FacesMessage.SEVERITY_ERROR, "Delete error", errorMessageContent(e));
        }
    }
}
