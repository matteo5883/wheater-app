package com.weather.monitoring.http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.weather.monitoring.metrics.WeatherMetricsCollector;
import com.weather.monitoring.health.HealthCheckManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * Simple HTTP server for exposing metrics and health endpoints
 */
@Slf4j
public class MonitoringHttpServer {

    private final HttpServer server;
    private final WeatherMetricsCollector metricsCollector;
    private final HealthCheckManager healthCheckManager;

    public MonitoringHttpServer(int port, WeatherMetricsCollector metricsCollector,
                               HealthCheckManager healthCheckManager) throws IOException {
        this.metricsCollector = metricsCollector;
        this.healthCheckManager = healthCheckManager;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(4));

        setupEndpoints();

        log.info("Monitoring HTTP server created on port {}", port);
    }

    private void setupEndpoints() {
        // Prometheus metrics endpoint
        server.createContext("/metrics", new MetricsHandler());

        // Health check endpoint
        server.createContext("/health", new HealthHandler());

        // Ready endpoint for Kubernetes readiness probes
        server.createContext("/ready", new ReadyHandler());

        // Live endpoint for Kubernetes liveness probes
        server.createContext("/live", new LiveHandler());

        // Info endpoint
        server.createContext("/info", new InfoHandler());
    }

    public void start() {
        server.start();
        log.info("Monitoring HTTP server started");
    }

    public void stop() {
        server.stop(5);
        log.info("Monitoring HTTP server stopped");
    }

    private class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method not allowed");
                return;
            }

            try {
                String metrics = metricsCollector.getMetrics();

                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                sendResponse(exchange, 200, metrics);

                log.debug("Served metrics endpoint");

            } catch (Exception e) {
                log.error("Error serving metrics", e);
                sendResponse(exchange, 500, "Internal server error");
            }
        }
    }

    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method not allowed");
                return;
            }

            try {
                HealthCheckManager.AggregatedHealthStatus health = healthCheckManager.checkAll();

                int statusCode = health.isHealthy() ? 200 : 503;
                String response = formatHealthResponse(health);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                sendResponse(exchange, statusCode, response);

                log.debug("Served health endpoint: status={}", health.getStatus());

            } catch (Exception e) {
                log.error("Error serving health check", e);
                sendResponse(exchange, 500, "{\"status\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    private class ReadyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                HealthCheckManager.AggregatedHealthStatus health = healthCheckManager.checkAll();

                // Service is ready if all critical components are working
                boolean ready = health.isHealthy() || "DEGRADED".equals(health.getStatus());

                int statusCode = ready ? 200 : 503;
                String response = "{\"status\":\"" + (ready ? "READY" : "NOT_READY") + "\"}";

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                sendResponse(exchange, statusCode, response);

            } catch (Exception e) {
                log.error("Error serving ready check", e);
                sendResponse(exchange, 503, "{\"status\":\"NOT_READY\"}");
            }
        }
    }

    private class LiveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Simple liveness check - if we can respond, we're alive
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            sendResponse(exchange, 200, "{\"status\":\"ALIVE\"}");
        }
    }

    private class InfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String info = "{\n" +
                "  \"application\": \"WeatherApp\",\n" +
                "  \"version\": \"1.0.0\",\n" +
                "  \"build_time\": \"" + System.currentTimeMillis() + "\",\n" +
                "  \"endpoints\": {\n" +
                "    \"/metrics\": \"Prometheus metrics\",\n" +
                "    \"/health\": \"Detailed health status\",\n" +
                "    \"/ready\": \"Readiness probe\",\n" +
                "    \"/live\": \"Liveness probe\"\n" +
                "  }\n" +
                "}";

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            sendResponse(exchange, 200, info);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String formatHealthResponse(HealthCheckManager.AggregatedHealthStatus health) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"status\": \"").append(health.getStatus()).append("\",\n");
        sb.append("  \"healthy\": ").append(health.isHealthy()).append(",\n");
        sb.append("  \"timestamp\": \"").append(health.getTimestamp()).append("\",\n");
        sb.append("  \"response_time_ms\": ").append(health.getTotalResponseTimeMs()).append(",\n");
        sb.append("  \"checks\": {\n");

        boolean first = true;
        for (var entry : health.getIndividualResults().entrySet()) {
            if (!first) sb.append(",\n");
            first = false;

            var status = entry.getValue();
            sb.append("    \"").append(entry.getKey()).append("\": {\n");
            sb.append("      \"status\": \"").append(status.getStatus()).append("\",\n");
            sb.append("      \"healthy\": ").append(status.isHealthy()).append(",\n");
            sb.append("      \"response_time_ms\": ").append(status.getResponseTimeMs());

            if (status.getMessage() != null) {
                sb.append(",\n      \"message\": \"").append(status.getMessage()).append("\"");
            }

            sb.append("\n    }");
        }

        sb.append("\n  }\n");
        sb.append("}");

        return sb.toString();
    }
}
