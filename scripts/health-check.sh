#!/bin/bash

# WeatherApp Health Check Script
# Verifies that all services are running correctly

set -e

echo "🏥 WeatherApp Health Check"
echo "========================="

# Check if services are running on expected ports
check_service() {
    local service_name=$1
    local port=$2
    local endpoint=$3

    echo -n "Checking $service_name (port $port)... "

    if curl -s -f "http://localhost:$port$endpoint" > /dev/null 2>&1; then
        echo "✅ OK"
        return 0
    else
        echo "❌ FAILED"
        return 1
    fi
}

# Check Backend
check_service "Backend API" 8080 "/actuator/health"

# Check Frontend (if running)
if lsof -Pi :4200 -sTCP:LISTEN -t >/dev/null 2>&1; then
    check_service "Frontend Web App" 4200 "/"
else
    echo "Frontend Web App (port 4200)... ⚠️  Not running"
fi

# Check Mobile (if running)
if lsof -Pi :8100 -sTCP:LISTEN -t >/dev/null 2>&1; then
    check_service "Mobile App Preview" 8100 "/"
else
    echo "Mobile App Preview (port 8100)... ⚠️  Not running"
fi

# Check API endpoints
echo ""
echo "🧪 Testing API Endpoints..."

# Test weather endpoint with mock data
echo -n "Weather API (mock)... "
if curl -s -f "http://localhost:8080/api/v1/weather/current?city=Milan&country=IT" > /dev/null 2>&1; then
    echo "✅ OK"
else
    echo "❌ FAILED"
fi

# Test alerts endpoint
echo -n "Alerts API... "
if curl -s -f "http://localhost:8080/api/v1/alerts" > /dev/null 2>&1; then
    echo "✅ OK"
else
    echo "❌ FAILED"
fi

# Test cache endpoint
echo -n "Cache API... "
if curl -s -f "http://localhost:8080/api/v1/cache/stats" > /dev/null 2>&1; then
    echo "✅ OK"
else
    echo "❌ FAILED"
fi

echo ""
echo "✅ Health check completed!"
echo ""
echo "🌐 Available Services:"
echo "   - Backend API: http://localhost:8080"
echo "   - API Documentation: http://localhost:8080/swagger-ui.html"
echo "   - Health Check: http://localhost:8080/actuator/health"
if lsof -Pi :4200 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "   - Frontend Web App: http://localhost:4200"
fi
if lsof -Pi :8100 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "   - Mobile App Preview: http://localhost:8100"
fi
