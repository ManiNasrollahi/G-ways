package com.Gways.configuration.security;

import com.Gways.gateway.users.service.GatewayUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RouteSecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;
    private final GatewayUsersService gatewayUsersService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(configurer -> configurer.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/panel/**")
                        .authenticated()
                        .anyRequest()
                        .permitAll()
                )
                .formLogin(cfg -> cfg.loginPage("/login.xhtml")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/panel/index.xhtml", true)
                        .failureUrl("/login.xhtml?error=true")
                        .permitAll()
                )
                .logout(cfg -> cfg
                        .logoutSuccessUrl("/login.xhtml?logout")
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .userDetailsService(gatewayUsersService);
        return http.build();
    }



}
