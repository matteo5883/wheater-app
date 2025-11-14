# 📊 WeatherApp Monitoring Stack

## 🚀 Complete Production Monitoring Solution

This project includes a complete monitoring system for WeatherApp using **Spring Boot Actuator**, Prometheus, AlertManager, and Grafana for enterprise-grade observability.

## 📦 Components Implemented

### 🔍 Health Check System (Spring Actuator)
- **Spring Boot Actuator** - Built-in health checks and metrics
- **Custom Health Indicators** - Weather API and cache monitoring via `MonitoringController`
- **Automatic health aggregation** - All system components monitored
- **Kubernetes probes support** - Ready-to-use readiness/liveness endpoints

### ⚡ Circuit Breaker Pattern
- **`CircuitBreaker`** - Custom implementation with CLOSED/OPEN/HALF_OPEN states
- **Flexible configuration** - Failure thresholds, timeout, retry logic
- **Integrated metrics** - Detailed statistics exposed via Micrometer
- **Used by `WeatherService`** - Protects external API calls

### 📈 Prometheus Metrics (Micrometer)
- **Micrometer Registry** - Standard metrics export for Prometheus
- **Spring Boot Actuator integration** - Automatic JVM and application metrics
- **Custom metrics** - Weather-specific metrics in `WeatherService` and `MonitoringController`
- **Available metrics:**
  - API response times and failure rates
  - Cache hit/miss ratios  
  - Circuit breaker states
  - JVM memory and thread counts
  - HTTP request metrics
  - Weather alert trigger rates

### 🌐 Spring Actuator Endpoints
- **`/actuator/health`** - Detailed health status with custom indicators
- **`/actuator/prometheus`** - Prometheus metrics endpoint
- **`/actuator/metrics`** - Individual metrics inspection
- **`/actuator/caches`** - Cache statistics
- **`/actuator/info`** - Application information

## 🏗️ Architecture

```
WeatherApp (Port 8080)
├── /actuator/prometheus  → Prometheus scraping
├── /actuator/health      → Health aggregation + custom indicators
├── /actuator/metrics     → Individual metrics
├── /actuator/caches      → Cache statistics
└── /actuator/info        → Application info

Prometheus (Port 9090)
├── Scrapes /actuator/prometheus endpoint
├── Evaluates alerting rules
└── Triggers alerts → AlertManager

AlertManager (Port 9093)  
├── Receives alerts from Prometheus
├── Groups and routes notifications
└── Sends emails/webhooks

Grafana (Port 3000)
├── Visualizes Prometheus data
├── Pre-configured dashboards
└── Real-time monitoring
```

## 🚀 Quick Start

### 1. Build Application
```bash
cd backend
./gradlew build
docker build -t weather-app .
```

### 2. Start Monitoring Stack
```bash
cd docker
export WEATHER_API_KEY=your_api_key_here
docker-compose -f docker-compose.monitoring.yml up -d
```

### 3. Access Dashboards
- **WeatherApp API**: http://localhost:8080
- **Spring Actuator Health**: http://localhost:8080/actuator/health
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus
- **Prometheus UI**: http://localhost:9090
- **AlertManager**: http://localhost:9093  
- **Grafana**: http://localhost:3000 (admin/admin)

## 📊 Available Metrics

### Application Metrics (Custom via Micrometer)
```prometheus
# API call metrics
weather_api_calls_duration_seconds_sum
weather_api_calls_duration_seconds_count
weather_api_calls_duration_seconds_max
weather_api_calls_total{operation, status}

# Circuit breaker metrics (available via MonitoringController)
# Exposed as part of health endpoint and custom gauges

# HTTP metrics (automatic via Spring Boot)
http_server_requests_seconds{method, uri, status, outcome}
http_server_requests_seconds_max{method, uri, status, outcome}
```

### System Metrics (Automatic via Micrometer)
```prometheus  
# JVM Memory
jvm_memory_used_bytes{area, id}
jvm_memory_committed_bytes{area, id}
jvm_memory_max_bytes{area, id}

# JVM Threads
jvm_threads_live_threads
jvm_threads_daemon_threads
jvm_threads_peak_threads
jvm_threads_states_threads{state}

# JVM GC
jvm_gc_pause_seconds_sum{action, cause}
jvm_gc_pause_seconds_count{action, cause}
jvm_gc_memory_allocated_bytes_total
jvm_gc_memory_promoted_bytes_total

# System
system_cpu_usage
system_cpu_count
process_cpu_usage
process_uptime_seconds
process_start_time_seconds

# Tomcat/HTTP Server
tomcat_sessions_active_current_sessions
tomcat_sessions_created_sessions_total
tomcat_threads_busy_threads
tomcat_threads_current_threads

# Cache (Spring Cache via Actuator)
cache_gets_total{cache, result}
cache_puts_total{cache}
cache_evictions_total{cache}
cache_size{cache}
```

### Health Check Metrics
Available via `/actuator/health` endpoint:
- Weather service health status
- Circuit breaker state
- Cache statistics
- Database connectivity (if applicable)
- Disk space
- Custom health indicators

## 🚨 Alerting Rules

### Critical Alerts
- **WeatherAppDown** - Application health failure > 1min
- **HighAPIFailureRate** - API failures > 10% for 2min
- **CircuitBreakerOpen** - Circuit breaker open > 30sec

### Warning Alerts  
- **WeatherAppDegraded** - API health degraded > 2min
- **HighAPIResponseTime** - Response time > 5sec for 3min
- **LowCacheHitRate** - Cache hit rate < 50% for 5min
- **HighMemoryUsage** - JVM memory > 85% for 2min

### Info Alerts
- **HighAlertTriggerRate** - Many weather alerts triggered
- **NoWeatherDataUpdates** - No API success in 10min

## 📧 AlertManager Configuration

### Email Notifications
```yaml
# Critical: Immediate notification to on-call
- severity: critical → oncall@company.com (30min repeat)

# Warning: Regular team notification  
- severity: warning → weather-team@company.com (2h repeat)

# Info: Low-priority team notification
- severity: info → weather-team@company.com (12h repeat)
```

### Webhook Integration
- **PagerDuty** integration for critical alerts
- **Slack** notifications via webhook
- **Custom webhook** endpoint support

## 🎯 Dashboard Features

### WeatherApp Dashboard (Grafana)
1. **Application Health** - Real-time health status indicator
2. **API Response Time** - Response time trends and percentiles
3. **Cache Performance** - Hit rates and cache size over time
4. **Circuit Breaker Status** - State changes and failure rates
5. **Request Rates** - API call volume by operation and status
6. **JVM Memory** - Memory usage and GC activity
7. **Weather Alerts** - Alert trigger frequency by type/location

### Key Performance Indicators
- **Availability**: `weather_health_status` uptime percentage
- **Performance**: P95 API response time < 2 seconds
- **Reliability**: API error rate < 1%
- **Efficiency**: Cache hit rate > 80%

## 🛠️ Configuration

### Spring Actuator Configuration
The application uses Spring Boot Actuator for monitoring. Configuration in `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,caches
  endpoint:
    health:
      show-details: always
      show-components: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 50ms,100ms,200ms,500ms
```

### Environment Variables
```bash
# Required for real API usage
WEATHER_API_KEY=your_openweathermap_key

# Optional configuration
WEATHER_MOCK_MODE=false
WEATHER_API_PROVIDER=openweathermap
WEATHER_CACHE_EXPIRATION_MINUTES=30
```

### Prometheus Configuration
- **Scrape interval**: 15 seconds
- **Retention**: 200 hours  
- **Alerting evaluation**: 15 seconds

### AlertManager Configuration
- **Group wait**: 10 seconds
- **Group interval**: 10 seconds  
- **Repeat interval**: 1 hour (varies by severity)

## 🔧 Customization

### Adding New Metrics (Micrometer)
```java
// Inject MeterRegistry in your component
@Service
@RequiredArgsConstructor
public class MyService {
    private final MeterRegistry meterRegistry;
    
    public void myMethod() {
        // Counter
        Counter.builder("custom_metric_total")
            .tag("type", "example")
            .register(meterRegistry)
            .increment();
        
        // Gauge
        Gauge.builder("custom_gauge", this, MyService::calculateValue)
            .tag("type", "example")
            .register(meterRegistry);
        
        // Timer
        Timer.builder("custom_operation_duration")
            .tag("operation", "example")
            .register(meterRegistry)
            .record(() -> {
                // Your timed operation
            });
    }
}
```

### Adding New Health Checks (Spring Actuator)
```java
// Implement HealthIndicator interface
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // Your health check logic
            boolean isHealthy = checkCustomComponent();
            
            return isHealthy 
                ? Health.up()
                    .withDetail("custom-component", "operational")
                    .build()
                : Health.down()
                    .withDetail("custom-component", "failed")
                    .build();
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}
```

### Adding New Alerts
```yaml
# In weather_app_rules.yml
- alert: CustomAlert
  expr: custom_metric_total > 100
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "Custom condition detected"
```

## 🚀 Production Deployment

### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: weather-app
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: weather-app
        image: weather-app:latest
        ports:
        - containerPort: 8080
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### Docker Swarm
```bash
docker stack deploy -c docker-compose.monitoring.yml weather-monitoring
```

### Scalability Considerations
- **Horizontal scaling**: Multiple app instances behind load balancer
- **Metrics aggregation**: Prometheus federation for multi-cluster
- **Alert deduplication**: AlertManager clustering
- **Dashboard scaling**: Grafana high availability setup

## 📝 Troubleshooting

### Common Issues

**Metrics not appearing**
- Check `/actuator/prometheus` endpoint returns data
- Verify Prometheus target is UP in Status → Targets
- Confirm scrape_configs in prometheus.yml points to `/actuator/prometheus`
- Ensure `management.endpoints.web.exposure.include` includes `prometheus` in application.yml

**Alerts not firing**  
- Check Prometheus → Alerts page for rule evaluation
- Verify alertmanager connectivity in Prometheus config
- Check AlertManager logs for routing issues

**High memory usage**
- Monitor `jvm_memory_used_bytes` metric
- Adjust `-XX:MaxRAMPercentage` in Dockerfile
- Check for cache size limits

**Circuit breaker issues**
- Monitor `weather_circuit_breaker_state` metric
- Check failure threshold configuration
- Verify upstream service health

---

**Monitoring Stack Complete! 🎉** 

Your WeatherApp now has enterprise-grade observability with Prometheus metrics, intelligent alerting, and beautiful Grafana dashboards!
