#!/bin/bash

echo "🧪 Testing WeatherApp Monitoring"
echo "================================"

BASE_URL="http://localhost:8080"

# Test health endpoint
echo "1️⃣  Testing health endpoint..."
if curl -f -s "$BASE_URL/health" | jq . > /dev/null 2>&1; then
    echo "   ✅ Health endpoint responding"
    curl -s "$BASE_URL/health" | jq '.status, .healthy'
else
    echo "   ❌ Health endpoint not responding"
fi

echo ""

# Test metrics endpoint
echo "2️⃣  Testing metrics endpoint..."
if curl -f -s "$BASE_URL/metrics" | head -5 > /dev/null; then
    echo "   ✅ Metrics endpoint responding"
    echo "   📊 Sample metrics:"
    curl -s "$BASE_URL/metrics" | grep -E "weather_|jvm_" | head -5
else
    echo "   ❌ Metrics endpoint not responding"
fi

echo ""

# Test readiness probe
echo "3️⃣  Testing readiness probe..."
if curl -f -s "$BASE_URL/ready" > /dev/null; then
    echo "   ✅ Application is ready"
    curl -s "$BASE_URL/ready" | jq .
else
    echo "   ❌ Application not ready"
fi

echo ""

# Test liveness probe
echo "4️⃣  Testing liveness probe..."
if curl -f -s "$BASE_URL/live" > /dev/null; then
    echo "   ✅ Application is alive"
    curl -s "$BASE_URL/live" | jq .
else
    echo "   ❌ Application not alive"
fi

echo ""

# Test weather API (if app is running)
echo "5️⃣  Testing weather functionality..."
if command -v jq &> /dev/null; then
    # If we had a REST API, we'd test it here
    echo "   💡 Weather CLI available - run: docker exec -it weather-app java -jar app.jar"
else
    echo "   💡 Install jq for JSON parsing: brew install jq"
fi

echo ""

# Check Prometheus targets
echo "6️⃣  Checking Prometheus targets..."
PROM_URL="http://localhost:9090"
if curl -f -s "$PROM_URL/api/v1/targets" > /dev/null; then
    echo "   ✅ Prometheus is scraping targets"
    # Show weather-app target status
    curl -s "$PROM_URL/api/v1/targets" | jq '.data.activeTargets[] | select(.labels.job == "weather-app") | {health: .health, lastScrape: .lastScrape}'
else
    echo "   ❌ Cannot reach Prometheus"
fi

echo ""

# Check for active alerts
echo "7️⃣  Checking active alerts..."
if curl -f -s "$PROM_URL/api/v1/alerts" > /dev/null; then
    ALERT_COUNT=$(curl -s "$PROM_URL/api/v1/alerts" | jq '.data.alerts | length')
    if [ "$ALERT_COUNT" -eq 0 ]; then
        echo "   ✅ No active alerts - system healthy"
    else
        echo "   ⚠️  $ALERT_COUNT active alert(s):"
        curl -s "$PROM_URL/api/v1/alerts" | jq '.data.alerts[] | {alert: .labels.alertname, severity: .labels.severity, state: .state}'
    fi
else
    echo "   ❌ Cannot check alerts"
fi

echo ""

# Performance test
echo "8️⃣  Running basic performance test..."
echo "   🔄 Making 10 requests to test caching and circuit breaker..."

for i in {1..10}; do
    if curl -f -s "$BASE_URL/health" > /dev/null; then
        echo -n "✅"
    else
        echo -n "❌"
    fi
done
echo ""
echo "   📊 Check metrics for cache hit rate and response times"

echo ""
echo "🎯 Monitoring URLs:"
echo "   • Grafana Dashboard:  http://localhost:3000"
echo "   • Prometheus Targets: http://localhost:9090/targets"
echo "   • AlertManager:       http://localhost:9093"
echo ""
echo "📈 Key metrics to watch:"
echo "   weather_health_status{} == 1"
echo "   weather_cache_hit_rate > 0.8"
echo "   weather_circuit_breaker_state{} == 0"
echo "   rate(weather_api_calls_duration_ms_sum[5m]) / rate(weather_api_calls_duration_ms_count[5m]) < 2000"
echo ""
