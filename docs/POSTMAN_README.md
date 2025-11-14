# WeatherApp Postman Collection

This collection contains all the REST endpoints available in the WeatherApp API for comprehensive testing.

## 📁 Files Included

- `WeatherApp-Postman-Collection.json` - Main collection with all endpoints
- `WeatherApp-Environment.json` - Environment variables for different setups

## 🚀 Quick Start

1. **Import Collection**
   - Open Postman
   - Click "Import" → "File" → Select `WeatherApp-Postman-Collection.json`

2. **Import Environment**
   - Click "Import" → "File" → Select `WeatherApp-Environment.json`
   - Select the "WeatherApp Environments" from the environment dropdown

3. **Start Application**
   ```bash
   ./gradlew bootRun
   ```

4. **Test Endpoints**
   - Collection is ready to use with localhost:8080

## 📋 API Endpoints Coverage

### 🌤️ Weather API (`/api/v1/weather`)
- **GET** `/current` - Get current weather for location
- **GET** `/forecast` - Get weather forecast (1-14 days)
- **GET** `/cache/stats` - Get cache statistics
- **DELETE** `/cache` - Clear weather cache
- **GET** `/health` - Weather service health check

### 🚨 Weather Alerts (`/api/v1/alerts`)
- **GET** `/` - Get all active alerts
- **GET** `/location` - Get alerts for specific location
- **POST** `/temperature` - Create temperature alert
- **POST** `/precipitation` - Create precipitation alert  
- **POST** `/wind` - Create wind speed alert
- **DELETE** `/{alertId}` - Delete specific alert

### 💾 Cache Management (`/api/v1/cache`) 
- **GET** `/stats` - Get all cache statistics
- **GET** `/stats/{cacheName}` - Get specific cache stats
- **DELETE** `/{cacheName}` - Clear specific cache
- **DELETE** `/all` - Clear all caches
- **GET** `/config` - Get cache configuration

### 📊 Monitoring (`/api/v1/monitoring`)
- **GET** `/health/detailed` - Detailed health information
- **GET** `/circuit-breaker` - Circuit breaker status
- **GET** `/metrics` - Application metrics
- **DELETE** `/cache/clear` - Clear monitoring cache

### 🔧 Spring Boot Actuator (`/actuator`)
- **GET** `/health` - Application health
- **GET** `/info` - Application information
- **GET** `/metrics` - Micrometer metrics
- **GET** `/prometheus` - Prometheus format metrics
- **GET** `/caches` - Cache information

## 🔧 Environment Variables

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8080` | Application base URL |
| `apiVersion` | `v1` | API version |
| `weatherApiKey` | `your-api-key-here` | Weather API key (if needed) |

## 📝 Example Requests

### Get Current Weather
```
GET {{baseUrl}}/api/{{apiVersion}}/weather/current
?city=Milan&country=IT&latitude=45.4642&longitude=9.1900
```

### Create Temperature Alert
```
POST {{baseUrl}}/api/{{apiVersion}}/alerts/temperature
Body:
{
  "location": {
    "city": "Milan",
    "country": "IT", 
    "latitude": 45.4642,
    "longitude": 9.1900
  },
  "threshold": 35.0,
  "operator": "GREATER_THAN",
  "description": "High temperature alert for Milan",
  "active": true
}
```

## ✅ Automated Tests

The collection includes basic automated tests:
- Status code validation (not 5xx)
- Response time check (< 5000ms)
- Response logging for debugging

## 🌍 Different Environments

### Development (Default)
- Base URL: `http://localhost:8080`
- Mock mode enabled
- Detailed logging

### Production
To test against production:
1. Duplicate the environment
2. Change `baseUrl` to production URL
3. Update `weatherApiKey` with real API key
4. Set appropriate timeout values

## 🐛 Troubleshooting

### Common Issues:

1. **Connection Refused**
   - Ensure application is running: `./gradlew bootRun`
   - Check port 8080 is available

2. **401/403 Errors**
   - Check if API key is required
   - Verify authentication headers

3. **Timeout Errors**
   - Increase request timeout in Postman
   - Check network connectivity

### Debug Tips:
- Check console logs in Postman for response details
- Use the detailed health endpoint for service status
- Monitor application logs during requests

## 📖 API Documentation

For complete API documentation with schemas and examples:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

**Happy Testing! 🚀**
