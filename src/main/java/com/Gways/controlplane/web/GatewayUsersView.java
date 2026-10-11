package com.Gways.controlplane.web;

import com.Gways.controlplane.web.base.BaseView;
import com.Gways.gateway.users.entity.GatewayUsers;
import com.Gways.controlplane.service.GatewayUsersAdminService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Named("gatewayUsersView")
@ViewScoped
@Getter
@Setter
public class GatewayUsersView extends BaseView {

    private final GatewayUsersAdminService adminService;
    private List<GatewayUsers> users;
    private GatewayUsers user;
    private String rawPassword;

    @Inject
    public GatewayUsersView(GatewayUsersAdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void initialize() {
        users = adminService.findAll();
        openNew();
    }

    public void openNew() {
        user = new GatewayUsers();
        user.setEnabled(true);
        rawPassword = null;
    }

    public void openEdit(Long id) {
        user = adminService.findById(id);
        rawPassword = null;
    }

    public void save() {
        try {
            adminService.save(user, rawPassword);
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Saved", "User has been saved");
        } catch (Exception exception) {
            message(FacesMessage.SEVERITY_ERROR, "Save error", errorMessageContent(exception));
        }
    }

    public void delete(GatewayUsers selectedUser) {
        try {
            adminService.delete(selectedUser.getId());
            initialize();
            message(FacesMessage.SEVERITY_INFO, "Deleted", "User has been deleted");
        } catch (Exception exception) {
            message(FacesMessage.SEVERITY_ERROR, "Delete error", errorMessageContent(exception));
        }
    }

    public long getEnabledCount() {
        return users.stream().filter(GatewayUsers::isEnabled).count();
    }
}
