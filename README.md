# Bank Account Microservice

A RESTful microservice for managing bank accounts built with Spring Boot and Jakarta EE.

## 📋 Overview

This microservice provides a comprehensive API for bank account management operations including account creation, retrieval, updates, and closure. It's designed following modern microservice architecture principles with a focus on reliability, scalability, and maintainability.

## ✨ Features

- **Account Management**: Create, read, update, and close bank accounts.
- **RESTful API**: Clean and intuitive REST endpoints.
- **API Documentation**: Interactive Swagger/OpenAPI documentation.
- **Data Persistence**: Spring Data JPA with database support.
- **Validation**: Jakarta Bean Validation for data integrity.
- **Exception Handling**: Comprehensive error handling and responses.

## 🛠️ Technology Stack

- **Java 21**: Latest Java SDK
- **Spring Boot**: Application framework
- **Spring MVC**: Web layer
- **Spring Data JPA**: Data persistence layer
- **Jakarta EE**: Enterprise Java specifications
- **Swagger/OpenAPI 3**: API documentation
- **Maven**: Dependency management and build tool
- **Docker**: Containerization

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.6+
- Docker

### Installation
 Clone the repository:
   ```bash
   git clone https://github.com/Dobrosav/bank-account-service.git
   ```



### Using Docker Compose

To run the service using Docker Compose, execute the following command in the root directory of the project:

```bash
docker-compose up
```

This will build the Docker image if it's not already built and start the application and the database. If you want to force a rebuild of the image, you can use:

```bash
docker-compose up --build
```

The application will be available at `http://localhost:11056`.

## 🧪 Running Tests

### Run All Tests

To execute all unit and integration tests:

```bash
mvn test
```

### Run Specific Test Classes

To run a specific test class:

```bash
mvn test -Dtest=AccountServiceTest
mvn test -Dtest=AccountControllerTest
```

### Run Tests with Coverage

To run tests and generate a coverage report:

```bash
mvn clean test jacoco:report
```

The coverage report will be generated in `target/site/jacoco/index.html`.

### Skip Tests During Build

To build the project without running tests:

```bash
mvn clean install -DskipTests
```

## 📖 API Documentation

The API documentation is available via Swagger UI at:

[http://localhost:11056/swagger-ui.html](http://localhost:11056/swagger-ui.html)

## ⚙️ Configuration

The application can be configured via the `application.properties` file located in `src/main/resources`.

Key configuration properties:

- `server.port`: The port on which the application will run.
- `spring.datasource.url`: The database connection URL.
- `spring.datasource.username`: The database username.
- `spring.datasource.password`: The database password.

## 🙌 Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue.

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a pull request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
