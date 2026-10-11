package com.Gways.controlplane.service;

import com.Gways.gateway.dto.ServerStatus;
import com.Gways.gateway.routing.entity.GatewayRoute;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class CheckRoutes {


    public Map<String, ServerStatus> checkServers(List<GatewayRoute> routes) {

        try(var executor = Executors.newVirtualThreadPerTaskExecutor()){

            Map<String, Future<ServerStatus>> pending = new LinkedHashMap<>();
            Map<String, ServerStatus> results = new LinkedHashMap<>();

            for (GatewayRoute route : routes) {
                pending.computeIfAbsent(route.getUri(), uri -> executor.submit(() -> checkServer(uri)));
            }
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
            for (var entry : pending.entrySet()) {
                try {
                    results.put(entry.getKey(), entry.getValue().get(Math.max(1, deadline - System.nanoTime()), TimeUnit.NANOSECONDS));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    results.put(entry.getKey(), new ServerStatus("UNKNOWN", null, "Check interrupted", LocalDateTime.now()));
                } catch (Exception e) {
                    entry.getValue().cancel(true);
                    results.put(entry.getKey(), new ServerStatus("TIMEOUT", null, "Check exceeded the time limit", LocalDateTime.now()));
                }
            }
            return results;

        }
    }

    public ServerStatus checkServer(String destination) {
        try {
            URI uri = URI.create(destination);
            String scheme = uri.getScheme();
            if (uri.getHost() == null || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                return new ServerStatus("UNKNOWN", null, "A valid HTTP(S) destination is required", LocalDateTime.now());
            }
            int port = uri.getPort() == -1 ? ("https".equalsIgnoreCase(scheme) ? 443 : 80) : uri.getPort();
            long start = System.nanoTime();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(uri.getHost(), port), 2000);
                return new ServerStatus("REACHABLE", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start), "TCP connection accepted", LocalDateTime.now());
            }
        } catch (java.net.SocketTimeoutException e) {
            return new ServerStatus("TIMEOUT", null, "Connection timed out", LocalDateTime.now());
        } catch (java.net.UnknownHostException e) {
            return new ServerStatus("UNREACHABLE", null, "Host could not be resolved", LocalDateTime.now());
        } catch (IllegalArgumentException | NullPointerException e) {
            return new ServerStatus("UNKNOWN", null, "Invalid destination", LocalDateTime.now());
        } catch (java.io.IOException e) {
            return new ServerStatus("UNREACHABLE", null, "TCP connection failed", LocalDateTime.now());
        }
    }
}
