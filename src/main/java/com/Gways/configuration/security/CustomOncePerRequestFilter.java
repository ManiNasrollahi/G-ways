package com.Gways.configuration.security;

import com.Gways.configuration.IpResolver;
import com.Gways.configuration.RuntimeConfiguration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomOncePerRequestFilter extends OncePerRequestFilter {

    private final RuntimeConfiguration runtimeConfiguration;
    private final IpResolver ipResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException, ServletException {

        List<String> blacklist = runtimeConfiguration.getBlacklist();
        String ip = ipResolver.resolveClientIp(request, runtimeConfiguration.getTrustedProxiesRegex());

        if (blacklist != null && blacklist.contains(ip)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "status": 403,
                    "error": "FORBIDDEN",
                    "message": "Client IP is blacklisted"
                }
                """);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
