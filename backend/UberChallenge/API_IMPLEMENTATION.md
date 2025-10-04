# Uber Challenge Backend API

## Overview
This is a Spring Boot backend application for the Uber Challenge project. It provides REST APIs for driver authentication, job management, earnings predictions, and route assessments with integration to an ML service.

## Implementation Summary

### Entities Created
1. **User** - Manages user authentication with username/password
2. **Driver** - Driver profile with rating, vehicle type, fuel type, home city, and earner type
3. **Order** - Represents jobs/orders with start/end locations, timestamps, and city information

### DTOs Created
#### Request DTOs:
- `LogInRequestDTO` - Login credentials
- `RegisterRequestDTO` - Registration with driver details
- `LocationDTO` - Latitude/longitude coordinates
- `RouteAssessmentRequestDTO` - Route assessment with current, pickup, and dropoff locations

#### Response DTOs:
- `JwtResponseDTO` - JWT token and driver ID
- `EarningsGraphResponseDTO` - 24-hour earnings predictions
- `HeatmapResponseDTO` - Heatmap data points
- `HeatmapPointDTO` - Individual heatmap point (x, y, value)
- `JobItemDTO` - Job details with earnings info
- `JobListResponseDTO` - List of available jobs
- `RouteAssessmentResponseDTO` - Time and earnings predictions for a route

### Controllers Implemented

#### AuthController (`/api/auth`)
- **POST /login** - Authenticates user and returns JWT token
- **POST /register** - Registers new user and driver profile

#### DriverController (`/api/driver`) - All endpoints require JWT authentication
- **GET /route** - Get 24-hour earnings graph based on current location
  - Params: `currentLat`, `currentLon`
- **GET /heatmap** - Get heatmap data for a given time
  - Params: `time` (e.g., "2025-10-04T14:00")
- **GET /jobs** - Get list of nearby jobs with assessments
  - Params: `currentLat`, `currentLon`
- **POST /route-assessment** - Assess a specific route
  - Body: `RouteAssessmentRequestDTO`

### Services

#### AuthService
- User registration with password hashing (BCrypt)
- Login authentication
- JWT token generation with driverId claim
- User and Driver entity management

#### DriverService
- Fetches driver information
- Integrates with ML service for predictions:
  - Earnings graph prediction
  - Heatmap generation
  - Route assessment
- Job filtering by distance (Haversine formula, 10km radius)
- Mock data fallback when ML service is unavailable

### Security Configuration
- JWT-based authentication using `JWTTokenValidatorFilter`
- Public endpoints: `/api/auth/login`, `/api/auth/register`
- All other endpoints require Bearer token authentication
- CORS enabled for local development
- Stateless session management

### Key Features

#### JWT Authentication
- Tokens include userId (subject) and driverId (claim)
- 24-hour expiration (configurable)
- Driver ID automatically extracted and available in controllers

#### ML Service Integration
- Configurable ML service URL (`ml.service.url`)
- Graceful fallback to mock data if ML service unavailable
- Endpoints assumed:
  - `POST /predict/graph` - Earnings predictions
  - `POST /predict/heatmap` - Heatmap data
  - `POST /predict/route` - Route assessment

#### Distance Calculation
- Haversine formula for calculating distance between coordinates
- Jobs filtered within 10km radius of current location

## Configuration

### application.yaml
```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345678
  expiration: 86400000  # 24 hours in milliseconds

ml:
  service:
    url: http://localhost:5000  # ML service endpoint
```

### Database
- PostgreSQL database
- Hibernate DDL auto-update enabled
- Tables: `users`, `drivers`, `orders`

## API Usage Examples

### 1. Register a New Driver
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "driver123",
  "password": "password123",
  "rating": 4.5,
  "earnerType": "full-time",
  "fuelType": "electric",
  "homeCity": "New York",
  "vehicleType": "sedan"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "driverId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 2. Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "driver123",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "driverId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 3. Get Earnings Graph
```bash
GET /api/driver/route?currentLat=40.7128&currentLon=-74.0060
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Response:
{
  "earnings": {
    "00:00": 15.5,
    "01:00": 12.3,
    "02:00": 10.8,
    ...
    "23:00": 18.2
  }
}
```

### 4. Get Nearby Jobs
```bash
GET /api/driver/jobs?currentLat=40.7128&currentLon=-74.0060
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Response:
{
  "jobs": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "from": {"lat": 40.7128, "lon": -74.0060},
      "to": {"lat": 40.7580, "lon": -73.9855},
      "potentialEarningCents": 1500,
      "earningRateCents": 3600,
      "startTimestamp": 1728057600000
    }
  ]
}
```

### 5. Assess Route
```bash
POST /api/driver/route-assessment
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "currentDriverLoc": {"lat": 40.7128, "lon": -74.0060},
  "pickupLoc": {"lat": 40.7580, "lon": -73.9855},
  "dropoffLoc": {"lat": 40.7489, "lon": -73.9680}
}

Response:
{
  "predictedTimeMinutes": 25,
  "expectedEarnNetCents": 1500,
  "moneyPerHourCents": 3600
}
```

## Running the Application

1. **Start PostgreSQL Database**
   ```bash
   docker-compose up -d
   ```

2. **Build the Application**
   ```bash
   mvnw clean install
   ```

3. **Run the Application**
   ```bash
   mvnw spring-boot:run
   ```

4. **Access the API**
   - Base URL: `http://localhost:8082`
   - Auth endpoints: `http://localhost:8082/api/auth/*`
   - Driver endpoints: `http://localhost:8082/api/driver/*`

## Dependencies
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Security
- PostgreSQL Driver
- JWT (JJWT 0.12.6)
- Lombok
- Spring Validation

## Notes and Considerations

### ML Service Integration
- The application expects an ML service at `http://localhost:5000` (configurable)
- If ML service is unavailable, mock data is returned
- ML request format includes all driver attributes and location data

### Security
- Passwords are hashed using BCrypt with strength 12
- JWT secret should be changed in production
- HTTPS should be enabled in production (currently configured but optional)

### Job Filtering
- Jobs are filtered by city (matching driver's home city)
- Distance filter uses 10km radius (configurable in `DriverService.MAX_RADIUS_KM`)

### Database Schema
- Hibernate auto-creates/updates tables based on entities
- For production, consider using Flyway migrations

## Future Enhancements
1. Convert String fields to Enums (EarnerType, FuelType, VehicleType)
2. Add City entity and proper foreign key relationships
3. Implement proper error handling with custom exceptions
4. Add pagination for job listings
5. Add caching for ML predictions
6. Add rate limiting for API endpoints
7. Add comprehensive logging and monitoring
8. Add unit and integration tests

