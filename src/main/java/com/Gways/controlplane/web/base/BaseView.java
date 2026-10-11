package com.Gways.controlplane.web.base;

import com.Gways.gateway.users.entity.GatewayUsers;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public abstract class BaseView implements Serializable {

    private static final DateTimeFormatter AUDIT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public abstract void initialize();

    public GatewayUsers getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (GatewayUsers) authentication.getPrincipal();
        }
        return null;
    }

    public void message(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public String errorMessageContent(Throwable throwable) {
        while (throwable.getCause() != null) throwable = throwable.getCause();
        return throwable.getMessage() == null ? "server error" : throwable.getMessage();
    }

    public String formatAuditDate(LocalDateTime value) {
        return value == null ? "-" : AUDIT_DATE_FORMATTER.format(value);
    }
}
