# GoPay Wallet Backend Service

A Spring Boot backend service that provides REST APIs for the GoPay Wallet Android application.

## Features

- User Authentication & Authorization
  - JWT-based authentication
  - Role-based access control
- Transaction Management
  - Send money
  - Request money
  - Transaction history
- Account Management
  - Balance tracking
  - User profile management
- Security
  - Password encryption
  - Token-based authentication
  - Request validation

## Technical Stack

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL 8.0
- Maven
- JWT (JSON Web Tokens)

## Project Structure

```plaintext
src/
├── main/
│   ├── java/
│   │   └── com.example.gopaywallet/
│   │       ├── config/           # Configuration classes
│   │       │   ├── AsyncConfig.java
│   │       │   ├── EmailConfig.java
│   │       │   └── SecurityConfig.java
│   │       ├── controller/       # REST controllers
│   │       │   ├── AuthController.java
│   │       │   ├── TestController.java
│   │       │   └── TransactionController.java
│   │       ├── dto/             # Data Transfer Objects
│   │       │   ├── LoginRequest.java
│   │       │   ├── LoginResponse.java
│   │       │   ├── RegisterRequest.java
│   │       │   ├── TransactionDTO.java
│   │       │   ├── TransactionRequest.java
│   │       │   └── UserDTO.java
│   │       ├── exception/       # Custom Exceptions
│   │       │   ├── AuthenticationException.java
│   │       │   └── UserAlreadyExistsException.java
│   │       ├── model/          # Domain Models
│   │       │   ├── ChangePasswordRequest.java
│   │       │   ├── ErrorResponse.java
│   │       │   ├── ForgotPasswordRequest.java
│   │       │   ├── ForgotPasswordResponse.java
│   │       │   ├── PasswordResetToken.java
│   │       │   ├── ResetPasswordRequest.java
│   │       │   ├── ResetPasswordResponse.java
│   │       │   ├── SuccessResponse.java
│   │       │   ├── Transaction.java
│   │       │   └── User.java
│   │       ├── repository/     # Data Access Layer
│   │       │   ├── PasswordResetTokenRepository.java
│   │       │   ├── TransactionRepository.java
│   │       │   └── UserRepository.java
│   │       ├── security/       # Security Components
│   │       │   ├── CustomUserDetailsService.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── JwtTokenProvider.java
│   │       └── service/        # Business Logic
│   │           ├── AuthService.java
│   │           ├── EmailService.java
│   │           ├── EmailServiceImpl.java
│   │           ├── PasswordResetService.java
│   │           ├── TransactionService.java
│   │           └── UserService.java
│   └── resources/
│       ├── application.properties
│       └── application-dev.properties
└── test/                      # Test classes
```

## Key Components

### Configuration
- `AsyncConfig`: Enables asynchronous operations
- `EmailConfig`: Email service configuration
- `SecurityConfig`: Security and JWT configuration

### Controllers
- `AuthController`: Handles authentication endpoints
- `TransactionController`: Manages transaction operations
- `TestController`: Server health check endpoint

### Services
- `AuthService`: Authentication and user management
- `EmailService`: Email notifications
- `TransactionService`: Transaction processing
- `PasswordResetService`: Password reset functionality

### Security
- `JwtTokenProvider`: JWT token generation and validation
- `JwtAuthenticationFilter`: JWT-based authentication filter
- `CustomUserDetailsService`: User details management

## Email Configuration

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Database Configuration

### H2 Database Setup
The application uses H2, an in-memory/file-based database that's perfect for development and testing.

1. Current Configuration (from application.properties):
```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:file:./data/demo-db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=admin

# Show SQL queries (optional)
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

### Accessing H2 Console
1. Start the Spring Boot application
2. Open browser and navigate to: http://localhost:8081/h2-console
3. Enter the following details:
   - JDBC URL: jdbc:h2:file:./data/demo-db
   - Username: sa
   - Password: admin
   - Driver Class: org.h2.Driver

### Database Management

1. Database Files Location
```plaintext
project-root/
└── data/
    ├── demo-db.mv.db    # Main database file
    └── demo-db.trace.db # Trace file (if enabled)
```

2. Backup and Restore
```bash
# Export database to SQL file
java -cp h2*.jar org.h2.tools.Script -url "jdbc:h2:file:./data/demo-db" -user sa -password admin -script backup.sql

# Import database from SQL file
java -cp h2*.jar org.h2.tools.RunScript -url "jdbc:h2:file:./data/demo-db" -user sa -password admin -script backup.sql
```

3. Reset Database
```bash
# Delete database files
rm -rf ./data/demo-db*

# Application will recreate database on next startup
```

### Database Schema

#### Users Table
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

#### Transactions Table
```sql
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    date_time TIMESTAMP NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    recipient_name VARCHAR(255),
    recipient_phone VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### Password Reset Tokens Table
```sql
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Common Database Operations

1. Query Examples
```sql
-- Find user by email
SELECT * FROM users WHERE email = 'user@example.com';

-- Get user's recent transactions
SELECT * FROM transactions 
WHERE user_id = 1 
ORDER BY date_time DESC 
LIMIT 5;

-- Get active password reset tokens
SELECT * FROM password_reset_token 
WHERE used = FALSE AND expiry_date > CURRENT_TIMESTAMP();
```

2. Troubleshooting

If you encounter database issues:

a. Check Connection
```sql
-- Test database connection
SELECT 1;
```

b. Check Tables
```sql
-- List all tables
SHOW TABLES;

-- Get table structure
SHOW COLUMNS FROM users;
SHOW COLUMNS FROM transactions;
```

c. Check Data
```sql
-- Count records
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM transactions;
```

### Development Tips

1. Enable SQL Logging
```properties
# Add to application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

2. Database Initialization
```properties
# Initialize database with data.sql
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

3. Data Migration
- Place SQL scripts in `src/main/resources/data.sql` for initial data
- Place schema updates in `src/main/resources/schema.sql`

4. Testing
```properties
# Use in-memory database for tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
```

## API Documentation

### Authentication APIs

#### Login
```http
POST /auth/login
Content-Type: application/json

Request:
{
    "email": "user@example.com",
    "password": "password123"
}

Response: 200 OK
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
        "id": 1,
        "fullName": "John Doe",
        "email": "user@example.com"
    }
}
```

#### Register
```http
POST /auth/register
Content-Type: application/json

Request:
{
    "fullName": "John Doe",
    "email": "user@example.com",
    "phoneNumber": "+1234567890",
    "password": "password123"
}

Response: 201 Created
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
        "id": 1,
        "fullName": "John Doe",
        "email": "user@example.com"
    }
}
```

### Transaction APIs

#### Get User Transactions
```http
GET /api/transactions?userId={userId}&page={page}&size={size}
Authorization: Bearer {jwt_token}

Response: 200 OK
{
    "content": [
        {
            "id": 1,
            "amount": 100.00,
            "type": "SEND",
            "recipientName": "Jane Doe",
            "timestamp": "2024-01-26T10:30:00"
        }
    ],
    "totalPages": 1,
    "totalElements": 1
}
```

## Setup Instructions

1. Prerequisites:
   - JDK 17
   - Maven 3.8.x
   - MySQL 8.0

2. Clone the repository:
```bash
git clone https://github.com/vinit-nair/be_capstone_project.git
cd be_capstone_project
```

3. Configure MySQL:
```sql
CREATE DATABASE gopay_db;
CREATE USER 'gopay_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON gopay_db.* TO 'gopay_user'@'localhost';
FLUSH PRIVILEGES;
```

4. Configure application.properties:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/gopay_db
spring.datasource.username=gopay_user
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server Configuration
server.port=8081

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Logging
logging.level.org.springframework.security=DEBUG
```

5. Build and run:
```bash
mvn clean install
java -jar target/gopay-backend-1.0.0.jar
```

## Security Configuration

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/test").permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/forgot-password").permitAll()
                .requestMatchers("/auth/reset-password").permitAll()
                .requestMatchers("/api/transactions/**").permitAll()
                .requestMatchers("/auth/**", "/api/**", "/h2-console/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            )
            .headers(headersConfigurer ->
                headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

## API Documentation with Swagger

### Setup

1. Add Swagger dependencies to your `pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

2. Add Swagger configuration:
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GoPay Wallet API")
                        .version("1.0")
                        .description("API documentation for GoPay Wallet backend services")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com")));
    }
}
```

3. Configure application.properties:
```properties
# Swagger UI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
```

### Accessing Swagger Documentation

1. API Documentation UI:
   - URL: http://localhost:8081/swagger-ui.html
   - Interactive API documentation and testing interface

2. OpenAPI Specification:
   - URL: http://localhost:8081/v3/api-docs
   - Raw OpenAPI specification in JSON format

### API Documentation Examples

Add these annotations to your controllers:

```java
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful login"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Implementation
    }

    @Operation(summary = "User registration", description = "Register new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input / Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Implementation
    }
}
```

### Available API Endpoints

The Swagger UI provides documentation for all available endpoints:

1. Authentication
   - POST /auth/login
   - POST /auth/register
   - POST /auth/forgot-password
   - POST /auth/reset-password

2. Transactions
   - GET /api/transactions
   - POST /api/transactions
   - GET /api/transactions/{id}

3. User Management
   - GET /api/users/profile
   - PUT /api/users/profile
   - POST /api/users/change-password

### Testing APIs via Swagger UI

1. Open Swagger UI at http://localhost:8081/swagger-ui.html
2. Click on any endpoint to expand it
3. Click "Try it out"
4. Fill in the required parameters
5. Click "Execute" to test the API

### Security in Swagger

For protected endpoints:
1. Click the "Authorize" button in Swagger UI
2. Enter your JWT token
3. All subsequent requests will include the authorization header

## Testing

Run tests using:
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Generate test coverage report
mvn verify
```

## Common Issues and Solutions

1. Database Connection Issues
```bash
# Check MySQL service status
sudo service mysql status

# Restart MySQL
sudo service mysql restart
```

2. JWT Token Issues
```java
// Implement token refresh mechanism
@PostMapping("/auth/refresh")
public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
    // Validate existing token
    // Generate new token
    // Return new token
}
```

3. CORS Issues
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

## Performance Optimization

1. Database Indexing
```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_transaction_date ON transactions(created_at);
```

2. Caching Implementation
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "transactions");
    }
}
```

## Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## License

[Your License Here]

## Contact

[Your Contact Information] 