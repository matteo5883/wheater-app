#!/bin/bash
set -e

echo "🌤️  Starting WeatherApp Monitoring Stack"
echo "========================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose is not installed. Please install it first."
    exit 1
fi

# Set default API key if not provided
if [ -z "$WEATHER_API_KEY" ]; then
    echo "⚠️  WEATHER_API_KEY not set. Using mock mode."
    export WEATHER_MOCK_MODE=true
else
    echo "✅ Using provided API key for live weather data"
    export WEATHER_MOCK_MODE=false
fi

# Build the application if jar doesn't exist
if [ ! -f "build/libs/WheatherApp-1.0-SNAPSHOT.jar" ]; then
    echo "📦 Building WeatherApp..."
    ./gradlew build
fi

# Create Docker image
echo "🐳 Building Docker image..."
docker build -t weather-app:latest .

# Start the monitoring stack
echo "🚀 Starting monitoring stack..."
cd docker
docker-compose -f docker-compose.monitoring.yml up -d

echo ""
echo "✅ Monitoring stack started successfully!"
echo ""
echo "📊 Available endpoints:"
echo "  • WeatherApp:     http://localhost:8080"
echo "  • Prometheus:     http://localhost:9090"
echo "  • AlertManager:   http://localhost:9093"
echo "  • Grafana:        http://localhost:3000 (admin/admin)"
echo ""
echo "🔍 Health checks:"
echo "  • Health:         http://localhost:8080/health"
echo "  • Metrics:        http://localhost:8080/metrics"
echo "  • Ready:          http://localhost:8080/ready"
echo "  • Live:           http://localhost:8080/live"
echo ""
echo "📈 Key metrics to monitor:"
echo "  • weather_health_status (0=DOWN, 1=UP)"
echo "  • weather_api_calls_duration_ms (response times)"
echo "  • weather_cache_hit_rate (cache performance)"
echo "  • weather_circuit_breaker_state (0=CLOSED, 1=OPEN)"
echo ""
echo "🚨 Alerting configured for:"
echo "  • Application health failures"
echo "  • High API response times (>5s)"
echo "  • High API failure rates (>10%)"
echo "  • Circuit breaker opening"
echo "  • Low cache hit rates (<50%)"
echo "  • High memory usage (>85%)"
echo ""

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check if WeatherApp is responding
if curl -f -s http://localhost:8080/health > /dev/null; then
    echo "✅ WeatherApp is healthy and responding"
else
    echo "⚠️  WeatherApp is starting up... (check docker logs if it doesn't come up)"
fi

# Check if Prometheus is responding
if curl -f -s http://localhost:9090/-/ready > /dev/null; then
    echo "✅ Prometheus is ready"
else
    echo "⚠️  Prometheus is starting up..."
fi

# Check if Grafana is responding
if curl -f -s http://localhost:3000/api/health > /dev/null; then
    echo "✅ Grafana is ready"
else
    echo "⚠️  Grafana is starting up..."
fi

echo ""
echo "🎯 Next steps:"
echo "  1. Open Grafana at http://localhost:3000 (admin/admin)"
echo "  2. Import the WeatherApp dashboard"
echo "  3. Test the application: ./test-weather-app.sh"
echo "  4. View logs: docker-compose -f docker/docker-compose.monitoring.yml logs -f"
echo ""
echo "🛑 To stop: docker-compose -f docker/docker-compose.monitoring.yml down"
echo ""
