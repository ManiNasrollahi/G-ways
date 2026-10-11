package com.Gways.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
@RequiredArgsConstructor
public class IpResolver {

    public boolean isTrusted(String ipAddress, String regex) {

        if (ipAddress == null || ipAddress.isBlank()
                || regex == null || regex.isEmpty()) {
            return false;
        }

        if (regex.isBlank()) {
            return false;
        }

        try {
            return Pattern.matches(regex, ipAddress);
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    public String resolveClientIp(ServerRequest request, String regex) {
        String ip = request.remoteAddress()
                .map(address ->
                        address.getAddress().getHostAddress()
                )
                .orElse("unknown");
        if (isTrusted(ip, regex)) {
            String forwardedFor = request.headers().firstHeader("X-Forwarded-For");

            if (forwardedFor != null && !forwardedFor.isBlank()) {
                return forwardedFor.split(",")[0].trim();
            }
        }
        return ip;
    }

    public String resolveClientIp(HttpServletRequest request, String regex) {
        String ip = request.getRemoteAddr();
        if (isTrusted(ip, regex)) {
            String forwardedFor = request.getHeader("X-Forwarded-For");

            if (forwardedFor != null && !forwardedFor.isBlank()) {
                return forwardedFor.split(",")[0].trim();
            }
        }
        return ip;
    }
}
