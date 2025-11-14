# 📊 WeatherApp Monitoring Stack

## 🚀 Complete Production Monitoring Solution

Questo progetto include un sistema di monitoring completo per WeatherApp con Prometheus, AlertManager, Grafana e health checks enterprise-grade.

## 📦 Componenti Implementati

### 🔍 Health Check System
- **`HealthCheckManager`** - Orchestratore per tutti gli health checks
- **`WeatherApiHealthCheck`** - Verifica connettività API esterna
- **`CacheHealthCheck`** - Monitoraggio performance cache
- **`SystemHealthCheck`** - Controllo risorse sistema (memoria, disco, CPU)

### ⚡ Circuit Breaker Pattern
- **`CircuitBreaker`** - Implementazione completa con stati CLOSED/OPEN/HALF_OPEN
- **Configurazione flessibile** - Soglie failure, timeout, retry logic
- **Metrics integrate** - Statistiche dettagliate per monitoring

### 📈 Prometheus Metrics
- **`MetricsCollector`** - Collector compatibile formato Prometheus
- **`WeatherMetricsCollector`** - Metrics specifiche per WeatherApp
- **Metriche disponibili:**
  - API response times e failure rates
  - Cache hit/miss ratios
  - Circuit breaker states
  - JVM memory e thread counts
  - Health check status
  - Weather alert trigger rates

### 🌐 HTTP Endpoints
- **`/metrics`** - Prometheus metrics endpoint
- **`/health`** - Detailed health status (JSON)
- **`/ready`** - Kubernetes readiness probe
- **`/live`** - Kubernetes liveness probe  
- **`/info`** - Application information

## 🏗️ Architettura

```
WeatherApp (Port 8080)
├── /metrics          → Prometheus scraping
├── /health           → Health aggregation
├── /ready            → K8s readiness
└── /live             → K8s liveness

Prometheus (Port 9090)
├── Scrapes WeatherApp metrics
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
- **WeatherApp**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **AlertManager**: http://localhost:9093  
- **Grafana**: http://localhost:3000 (admin/admin)

## 📊 Available Metrics

### Application Metrics
```prometheus
# Health status (0=DOWN, 1=UP)
weather_health_status

# API response times
weather_api_calls_duration_ms_sum
weather_api_calls_duration_ms_count

# API request counts  
weather_api_requests_total{operation, status}

# Cache performance
weather_cache_hit_rate
weather_cache_size
weather_cache_hits_total
weather_cache_misses_total

# Circuit breaker state (0=CLOSED, 0.5=HALF_OPEN, 1=OPEN)
weather_circuit_breaker_state{name}
weather_circuit_breaker_failure_rate{name}
weather_circuit_breaker_total_requests{name}

# Weather alerts
weather_alerts_total{type, location, triggered}
```

### System Metrics
```prometheus  
# JVM memory
jvm_memory_used_bytes
jvm_memory_max_bytes
jvm_threads_current

# Application uptime
weather_app_uptime_seconds
```

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

### Adding New Metrics
```java
// In your code
metricsCollector.incrementCounter("custom_metric_total", "label", "value");
metricsCollector.setGauge("custom_gauge", 42.0, "label", "value");
metricsCollector.recordHistogram("custom_histogram", duration);
```

### Adding New Health Checks
```java
// Implement HealthCheck interface
public class CustomHealthCheck implements HealthCheck {
    public HealthStatus check() {
        // Your health check logic
        return HealthStatus.up("custom-component");
    }
}

// Register with manager
healthCheckManager.registerHealthCheck(new CustomHealthCheck());
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
            path: /ready
            port: 8080
          initialDelaySeconds: 30
        livenessProbe:
          httpGet:
            path: /live  
            port: 8080
          initialDelaySeconds: 30
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
- Check `/metrics` endpoint returns data
- Verify Prometheus target is UP in Status → Targets
- Confirm scrape_configs in prometheus.yml

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
