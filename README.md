# RidePally

RidePally is a social platform for motorcycle enthusiasts. The application enables riders to connect with fellow enthusiasts in their area, create events and join events such as group rides and meetups, and build a community around their shared passion for motorcycling.

## Features

- User authentication and authorization with JWT
- Role-based access control (User, Admin, Super Admin)
- User profile management
- Motorcycle management (add, view, delete motorcycles)
- Event management (create and join group rides)
- Location-based rider discovery
- RESTful API with OpenAPI documentation

## Technology Stack

- Java 21
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- PostgreSQL Database
- JWT for authentication
- Lombok for reducing boilerplate code
- Testcontainers for integration testing
- OpenAPI/Swagger for API documentation

## Architecture

The application follows a layered architecture pattern:

```
+------------------------+
|   Presentation Layer   |
|------------------------|
| Controllers            |
| DTOs                   |
| Mappers                |
+------------------------+
           ||
           ||
+------------------------+
|    Business Layer      |
|------------------------|
| Services               | 
| Exceptions             |
+------------------------+
           ||
           ||
+------------------------+
|      Data Layer        |
|------------------------|
| Repositories           |
| Database               |
+------------------------+
           ||
           ||
+------------------------+
|    Security Layer      |
|------------------------|
| JWT Auth               | 
| Spring Security        |
+------------------------+
```

### Layer Descriptions

1. **Presentation Layer**
   - Controllers: Handle HTTP requests and responses
   - DTOs: Data Transfer Objects for request/response handling
   - Mappers: Convert between DTOs and domain entities

2. **Business Layer**
   - Services: Implement business logic and rules
   - Exceptions: Custom exception handling

3. **Data Layer**
   - Repositories: Data access and persistence
   - Database: PostgreSQL for data storage

4. **Security Layer**
   - JWT Authentication: Token-based authentication
   - Spring Security: Authorization and security rules

## Getting Started

### Prerequisites

- JDK 21
- Maven 3.8+
- Docker (for running Testcontainers)

### Database Setup

1. Install PostgreSQL (if not already installed):
   ```bash
   # For macOS using Homebrew
   brew install postgresql@15
   brew services start postgresql@15
   ```

2. Create the database:
   ```bash
   createdb ridepally
   ```

3. Configure the application:
   - The application will automatically create the necessary tables on startup
   - Default database configuration in `application.yml`:
     ```yaml
     spring:
       datasource:
         url: jdbc:postgresql://localhost:5432/ridepally
         username: postgres
         password: postgres
       jpa:
         hibernate:
           ddl-auto: update
     ```
   - Update these values in `application.yml` if your PostgreSQL setup differs

4. Optional: Using Docker for PostgreSQL:
   ```bash
   docker run --name ridepally-db \
     -e POSTGRES_DB=ridepally \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -p 5432:5432 \
     -d postgres:15
   ```

### Running in IntelliJ IDEA

1. Open the project:
   - Launch IntelliJ IDEA
   - Select "Open" and navigate to the project directory
   - Wait for the project to load and index

2. Configure JDK:
   - Go to File → Project Structure (⌘;)
   - Under Project Settings → Project:
     - Set Project SDK to Java 21
     - Set Language level to 21
   - Under Project Settings → Modules:
     - Set Language level to 21
     - Ensure the project is marked as a Maven project

3. Configure Maven:
   - Open the Maven tool window (⌘9)
   - Click the "Reload All Maven Projects" button
   - Wait for dependencies to download

4. Run the application:
   - Locate `RidePallyApplication.java` in the Project view
   - Right-click and select "Run 'RidePallyApplication'"
   - Alternatively, click the green play button in the main class
   - The application will start and be available at `http://localhost:8080`

5. Debug the application:
   - Set breakpoints by clicking in the gutter next to line numbers
   - Right-click `RidePallyApplication.java` and select "Debug 'RidePallyApplication'"
   - Use the Debug tool window to inspect variables and step through code

6. View logs:
   - Open the Run/Debug tool window (⌘4)
   - Switch to the "Log" tab to see application output
   - Use the search functionality to filter logs

### Running the Application

1. Clone the repository
2. Build the project:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will start at `http://localhost:8080`

### API Documentation

When the application is running, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

The API documentation includes detailed information about all endpoints, including:

#### Motorcycle Management

- `POST /api/motorcycles` - Add a new motorcycle
- `POST /api/motorcycles/bulk` - Add multiple motorcycles at once (up to 10)
- `GET /api/motorcycles` - Get all motorcycles for the current user
- `GET /api/motorcycles/{motorcycleId}` - Get a specific motorcycle
- `DELETE /api/motorcycles/{motorcycleId}` - Delete a motorcycle

All motorcycle endpoints require authentication and proper authorization.

## Testing

The project uses Testcontainers for integration testing with PostgreSQL:

```
mvn test
```

## Development

This project uses Testcontainers during development time for database services.
Testcontainers has been configured to use the PostgreSQL Docker image.

## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.5/maven-plugin)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.5/reference/web/spring-security.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.5/reference/web/servlet.html)
* [Testcontainers](https://java.testcontainers.org/)