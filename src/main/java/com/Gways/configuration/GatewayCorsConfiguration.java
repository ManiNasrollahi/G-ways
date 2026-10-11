package com.Gways.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class GatewayCorsConfiguration {

    private final RuntimeConfiguration runtimeConfiguration;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return _ -> runtimeConfiguration.getCorsConfiguration();
    }
}
