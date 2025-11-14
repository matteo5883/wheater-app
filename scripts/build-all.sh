#!/bin/bash

# Build all projects for production
# Usage: ./build-all.sh

set -e

echo "🏗️  Building WeatherApp for Production"
echo "===================================="

# Build Backend
echo "🌐 Building Spring Boot backend..."
cd backend
./gradlew clean build -x test  # Skip tests for faster build
if [ $? -eq 0 ]; then
    echo "✅ Backend build successful"
else
    echo "❌ Backend build failed"
    exit 1
fi
cd ..

# Build Frontend
echo ""
echo "🅰️  Building Angular frontend..."
if [ -d "frontend/src" ]; then
    cd frontend
    npm run build --prod
    if [ $? -eq 0 ]; then
        echo "✅ Frontend build successful"
    else
        echo "❌ Frontend build failed"
        exit 1
    fi
    cd ..
else
    echo "⚠️  Frontend not set up yet"
fi

# Build Mobile
echo ""
echo "📱 Building Ionic mobile app..."
if [ -d "mobile/src" ]; then
    cd mobile
    npm run build
    if [ $? -eq 0 ]; then
        echo "✅ Mobile build successful"
    else
        echo "❌ Mobile build failed"
        exit 1
    fi
    cd ..
else
    echo "⚠️  Mobile app not set up yet"
fi

echo ""
echo "✅ All builds completed successfully!"
echo ""
echo "📦 Build artifacts:"
echo "   - Backend: backend/build/libs/"
echo "   - Frontend: frontend/dist/"
echo "   - Mobile: mobile/dist/"
