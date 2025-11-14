# Third-Party Licenses

This project uses the following third-party libraries and frameworks:

## Backend Dependencies (Java/Spring Boot)

### Core Frameworks
- **Spring Boot** - Apache License 2.0
  - https://spring.io/projects/spring-boot
  - Spring Boot Starter Web, Actuator, Cache, Validation, AOP

- **Spring Framework** - Apache License 2.0
  - https://spring.io/projects/spring-framework

### Utilities
- **Lombok** - MIT License
  - https://projectlombok.org/

- **Jackson** - Apache License 2.0
  - https://github.com/FasterXML/jackson
  - JSON serialization/deserialization

- **Caffeine Cache** - Apache License 2.0
  - https://github.com/ben-manes/caffeine
  - High-performance caching library

### Monitoring & Metrics
- **Micrometer** - Apache License 2.0
  - https://micrometer.io/
  - Application metrics facade

- **Prometheus Client** - Apache License 2.0
  - https://github.com/prometheus/client_java

### HTTP Client
- **OkHttp** - Apache License 2.0
  - https://square.github.io/okhttp/
  - HTTP & HTTP/2 client

- **Spring WebFlux** - Apache License 2.0
  - Reactive web client

### Documentation
- **SpringDoc OpenAPI** - Apache License 2.0
  - https://springdoc.org/
  - Swagger/OpenAPI documentation generator

### Testing
- **JUnit 5** - Eclipse Public License 2.0
  - https://junit.org/junit5/

- **Spring Boot Test** - Apache License 2.0
  - Testing utilities for Spring Boot

## Frontend Dependencies (Angular)

### Framework
- **Angular** - MIT License
  - https://angular.io/
  - Web application framework

### Build Tools
- **TypeScript** - Apache License 2.0
  - https://www.typescriptlang.org/

- **Webpack** - MIT License
  - Module bundler (used internally by Angular CLI)

## Mobile Dependencies (Ionic)

### Framework
- **Ionic Framework** - MIT License
  - https://ionicframework.com/
  - Mobile UI framework

- **Capacitor** - MIT License
  - https://capacitorjs.com/
  - Native runtime for web apps

## Infrastructure & DevOps

### Monitoring Stack
- **Prometheus** - Apache License 2.0
  - https://prometheus.io/
  - Monitoring and alerting toolkit

- **Grafana** - AGPL-3.0 License
  - https://grafana.com/
  - Visualization and analytics platform

- **AlertManager** - Apache License 2.0
  - https://prometheus.io/docs/alerting/latest/alertmanager/
  - Alert routing and management

### Containerization
- **Docker** - Apache License 2.0
  - https://www.docker.com/

## Development Tools

- **Gradle** - Apache License 2.0
  - https://gradle.org/
  - Build automation tool

- **npm** - Artistic License 2.0
  - https://www.npmjs.com/
  - Package manager

## External Services

- **OpenWeatherMap API** - Commercial API
  - https://openweathermap.org/api
  - Weather data provider
  - Requires API key (not included)

---

## License Compliance Notes

### MIT License
Permissive license that allows:
- ✅ Commercial use
- ✅ Modification
- ✅ Distribution
- ✅ Private use

Requirements:
- ⚠️ Include license and copyright notice

### Apache License 2.0
Permissive license that allows:
- ✅ Commercial use
- ✅ Modification
- ✅ Distribution
- ✅ Patent use
- ✅ Private use

Requirements:
- ⚠️ Include license and copyright notice
- ⚠️ State changes made to the code
- ⚠️ Include NOTICE file if provided

### AGPL-3.0 (Grafana)
Strong copyleft license:
- ✅ Commercial use allowed
- ✅ Modification allowed
- ⚠️ **Network use counts as distribution** - must provide source code
- Note: Grafana is used only for monitoring/visualization, not embedded in the application

### Eclipse Public License 2.0 (JUnit 5)
Weak copyleft license:
- ✅ Commercial use allowed
- ✅ Modification allowed
- ⚠️ Modified files must use EPL
- Note: JUnit is used only for testing, not distributed with the application

---

## Full License Texts

Full license texts for all dependencies can be found at:
- MIT: https://opensource.org/licenses/MIT
- Apache 2.0: https://www.apache.org/licenses/LICENSE-2.0
- AGPL-3.0: https://www.gnu.org/licenses/agpl-3.0.html
- EPL-2.0: https://www.eclipse.org/legal/epl-2.0/

---

**Last Updated**: November 14, 2025

For the most up-to-date dependency information, run:
```bash
# Backend dependencies
cd backend && ./gradlew dependencies

# Frontend dependencies  
cd frontend && npm list

# Mobile dependencies
cd mobile && npm list
```

