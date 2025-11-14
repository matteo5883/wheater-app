#!/bin/bash

# WeatherApp Development Environment Starter
# Usage: ./start-dev.sh

set -e

echo "🌤️  Starting WeatherApp Development Environment"
echo "=============================================="

# Check if projects are set up
if [ ! -d "frontend/src" ] || [ ! -d "mobile/src" ]; then
    echo "❌ Projects not set up. Run './scripts/setup-project.sh' first"
    exit 1
fi

# Function to check if port is available
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "❌ Port $1 is already in use"
        return 1
    fi
    return 0
}

# Check required ports
echo "🔍 Checking ports availability..."
check_port 8080 || exit 1  # Backend
check_port 4200 || exit 1  # Frontend
check_port 8100 || exit 1  # Mobile

echo "✅ All ports available"

# Start services
echo ""
echo "🚀 Starting all services..."
echo "   - Backend (Spring Boot): http://localhost:8080"
echo "   - Frontend (Angular): http://localhost:4200"
echo "   - Mobile (Ionic): http://localhost:8100"
echo ""
echo "Press Ctrl+C to stop all services"
echo ""

# Use npm script to start all services with concurrently
npm run dev
