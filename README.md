# 🌤️ WeatherApp Monorepo

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Complete weather application with Spring Boot backend, Angular frontend, and Ionic mobile app.

## 🏗️ Architecture

```
WeatherApp/
├── backend/          # Spring Boot REST API (Port 8080)
├── frontend/         # Angular Web App (Port 4200)
├── mobile/           # Ionic Mobile App (Port 8100)
├── scripts/          # Build and deployment scripts
└── docs/             # Documentation and API collections
```

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (for Spring Boot backend)
- **Node.js 18+** (for Angular and Ionic)
- **npm 9+** (package manager)

### Setup & Run
```bash
# 1. Setup all projects
./scripts/setup-project.sh

# 2. Configure environment
cp .env.example .env
# Edit .env with your API keys

# 3. Start all services
./scripts/start-dev.sh
```

### Manual Setup (Alternative)
```bash
# Install dependencies
npm install
npm run install:all

# Start all services
npm run dev
```

## 📱 Development URLs

- **🌐 Backend API**: http://localhost:8080
- **📱 Web App**: http://localhost:4200
- **📱 Mobile Preview**: http://localhost:8100
- **📚 API Docs**: http://localhost:8080/swagger-ui.html
- **💚 Health Check**: http://localhost:8080/actuator/health

## 🛠️ Available Scripts

```bash
# Development
npm run dev                    # Start all services with hot reload
npm run start:backend         # Start only Spring Boot backend
npm run start:frontend        # Start only Angular frontend  
npm run start:mobile          # Start only Ionic mobile app

# Building
npm run build:all             # Build all projects for production
npm run build:backend         # Build only backend
npm run build:frontend        # Build only frontend
npm run build:mobile          # Build only mobile

# Testing
npm run test:all              # Run all tests
npm run test:backend          # Run backend tests
npm run test:frontend         # Run frontend tests
npm run test:mobile           # Run mobile tests

# Installation
npm run install:all           # Install all dependencies
npm run install:frontend      # Install frontend dependencies
npm run install:mobile        # Install mobile dependencies
```

## 🐳 Docker

```bash
# Run everything with Docker
docker-compose up

# Build all images
docker-compose build

# Run in background
docker-compose up -d
```

## 📦 Project Structure

### Backend (Spring Boot)
```
backend/
├── src/main/java/com/weather/
│   ├── api/controller/       # REST controllers
│   ├── service/             # Business logic
│   ├── model/               # Data models
│   ├── config/              # Configuration
│   └── notification/        # Real-time notifications
├── src/main/resources/
│   ├── application.yml      # Main config
│   └── application-*.yml    # Environment configs
└── build.gradle            # Dependencies and build
```

### Frontend (Angular)
```
frontend/
├── src/app/
│   ├── components/          # UI components
│   ├── services/           # API services  
│   ├── models/             # TypeScript interfaces
│   └── pages/              # Page components
├── src/assets/             # Static assets
└── angular.json            # Angular configuration
```

### Mobile (Ionic)
```
mobile/
├── src/app/
│   ├── pages/              # Mobile pages
│   ├── components/         # Reusable components
│   ├── services/           # API and device services
│   └── models/             # TypeScript interfaces
├── android/                # Android build files
├── ios/                    # iOS build files  
└── capacitor.config.ts     # Capacitor configuration
```

## 🔧 Configuration

### Environment Variables
Create `.env` file in root:
```env
# Weather API
WEATHER_API_KEY=your-openweathermap-api-key
WEATHER_MOCK_MODE=false

# Notifications (optional)
FIREBASE_SERVER_KEY=your-firebase-server-key
APNS_KEY_ID=your-apple-push-key-id
```

### Backend Configuration
- **Config**: `backend/src/main/resources/application.yml`
- **Profiles**: development, production, docker
- **API Key**: Set via `WEATHER_API_KEY` environment variable

### Frontend Configuration
- **Environment**: `frontend/src/environments/`
- **API Base URL**: Automatically configured for each environment
- **Proxy Config**: Available for development CORS issues

### Mobile Configuration  
- **Config**: `mobile/capacitor.config.ts`
- **Platforms**: iOS and Android ready
- **Native Features**: Push notifications, geolocation, camera

## 🚀 Deployment

### Development
```bash
./scripts/start-dev.sh
```

### Production Build
```bash
./scripts/build-all.sh
```

### Docker Production
```bash
docker-compose -f docker-compose.prod.yml up
```

## 📱 Mobile Development

### iOS
```bash
cd mobile
ionic capacitor add ios
ionic capacitor run ios
```

### Android
```bash
cd mobile
ionic capacitor add android
ionic capacitor run android
```

## 🧪 Testing

### Backend Tests
- **Unit Tests**: JUnit 5
- **Integration Tests**: Spring Boot Test
- **API Tests**: RestTemplate/WebMvcTest

### Frontend Tests
- **Unit Tests**: Jasmine + Karma
- **E2E Tests**: Cypress (configurable)

### Mobile Tests
- **Unit Tests**: Jasmine + Karma
- **Device Testing**: Ionic DevApp

## 📚 Documentation

- **API Documentation**: Available at `/swagger-ui.html`
- **Postman Collection**: `docs/WeatherApp-Postman-Collection.json`
- **Architecture**: `docs/README.md`
- **Mobile Integration**: `docs/MOBILE_DESKTOP_INTEGRATION.md`

## 🤝 Contributing

1. Clone the repository
2. Run `./scripts/setup-project.sh`
3. Create feature branch
4. Make changes
5. Run tests: `npm run test:all`
6. Submit pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Third-party dependencies and their licenses are documented in [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md).

---

**Happy coding! 🚀**
