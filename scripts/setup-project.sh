#!/bin/bash

# WeatherApp Monorepo Setup Script
# Usage: ./setup-project.sh

set -e

echo "🌤️  WeatherApp Monorepo Setup"
echo "============================="

# Check prerequisites
echo "🔍 Checking prerequisites..."

if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18+ first."
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed. Please install npm first."
    exit 1
fi

echo "✅ Node.js version: $(node --version)"
echo "✅ npm version: $(npm --version)"

# Install root dependencies
echo ""
echo "📦 Installing root dependencies..."
npm install

# Setup Angular Frontend
echo ""
echo "🅰️  Setting up Angular frontend..."
if [ ! -d "frontend/src" ]; then
    echo "Creating Angular project in frontend directory..."
    cd frontend
    npx @angular/cli@latest new weather-web-app \
        --routing=true \
        --style=scss \
        --skip-git=true \
        --package-manager=npm \
        --directory=. \
        --strict=true \
        --standalone=true
    cd ..
    echo "✅ Angular project created with standalone components"
else
    echo "ℹ️  Angular project already exists"
fi

# Setup Ionic Mobile
echo ""
echo "📱 Setting up Ionic mobile app..."
if [ ! -d "mobile/src" ]; then
    echo "Installing Ionic CLI..."
    npm install -g @ionic/cli

    echo "Creating Ionic project in mobile directory..."
    cd mobile
    ionic start weather-mobile-app tabs \
        --type=angular \
        --capacitor \
        --skip-git \
        --package-manager=npm

    # Move files from subdirectory to mobile root
    if [ -d "weather-mobile-app" ]; then
        mv weather-mobile-app/* .
        mv weather-mobile-app/.[^.]* . 2>/dev/null || true
        rmdir weather-mobile-app
    fi
    cd ..
    echo "✅ Ionic project created"
else
    echo "ℹ️  Ionic project already exists"
fi

# Install all dependencies
echo ""
echo "📦 Installing all project dependencies..."
npm run install:all

echo ""
echo "✅ Setup completed successfully!"
echo ""
echo "🚀 Next steps:"
echo "   1. Configure your API key: cp .env.example .env"
echo "   2. Start development: npm run dev"
echo "   3. Visit:"
echo "      - Backend API: http://localhost:8080"
echo "      - Web App: http://localhost:4200"
echo "      - Mobile App: http://localhost:8100"
echo "      - API Docs: http://localhost:8080/swagger-ui.html"
