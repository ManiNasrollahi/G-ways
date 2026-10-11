package com.Gways.gateway;

import com.Gways.configuration.RuntimeConfiguration;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class Registry implements RouterFunction<ServerResponse> {

    private final RuntimeConfiguration runtimeConfiguration;
    private final RouteBuilder routeBuilder;

    private final AtomicReference<List<RouterFunction<ServerResponse>>> routeSnapshot = new AtomicReference<>(List.of());

    @PostConstruct
    public void initialize() {
        runtimeConfiguration.refreshRouteSnapshot();
        routeSnapshot.set(List.copyOf(routeBuilder.build(runtimeConfiguration.getRouteSnapshot())));
    }

    @Override
    @NullMarked
    public Optional<HandlerFunction<ServerResponse>> route (ServerRequest request) {

        for (RouterFunction<ServerResponse> function : routeSnapshot.get()) {

            Optional<HandlerFunction<ServerResponse>> response =
                    function.route(request);

            if (response.isPresent()) {
                return response;
            }
        }

        return Optional.empty();
    }

}
