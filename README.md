# Bank Account Microservices Ecosystem

An Event-Driven Microservices ecosystem for managing bank accounts built with Spring Boot, Jakarta EE, and Apache Kafka.

## 📋 Overview

This repository demonstrates a modern, scalable enterprise architecture. It features an event-driven microservices ecosystem designed as a Maven multi-module project. It utilizes Apache Kafka for asynchronous, decoupled communication between services, ensuring high performance and fault tolerance.

## 🏗️ Architecture: From Monolith to Event-Driven

This project showcases the evolution from a traditional monolithic application to a decoupled, event-driven architecture. 

The system currently consists of two independent microservices communicating asynchronously via a message broker:

1. **Bank Account Core (`bank-account-core`)**: The primary **Producer** service. It handles HTTP requests, executes core business logic (creating accounts, processing deposits/withdrawals), persists data to an MS SQL database, and publishes `TransactionEvent` messages to a Kafka topic.
2. **Notification Service (`notification-service`)**: The **Consumer** service. It independently listens to the Kafka topic. If it detects a specific business event (e.g., a withdrawal exceeding a defined threshold of 10,000), it triggers a notification workflow without impacting or blocking the core banking service.

**Message Broker:** Apache Kafka (running in modern KRaft mode).

---

## 🛠️ Implementation Guide (Step-by-Step Evolution)

If you are exploring this codebase, here is a breakdown of how the event-driven architecture was implemented:

### Step 1: Maven Multi-Module Restructuring
To keep the codebase organized within a single repository (Monorepo), a parent `pom.xml` with `<packaging>pom</packaging>` was created. The core application logic was moved to the `bank-account-core` module, and a new `notification-service` module was scaffolded.

### Step 2: Infrastructure via Docker Compose
Instead of relying on local installations, the infrastructure was fully containerized. The `docker-compose.yml` was updated to include:
* **MS SQL Server** for data persistence.
* **Apache Kafka** (`apache/kafka:3.7.0`) running in **KRaft mode** (eliminating the need for Zookeeper).

### Step 3: The Producer (Core Service)
* Added `spring-kafka` dependency.
* Configured `KafkaTemplate` with `JsonSerializer`.
* Created a `TransactionEventDTO` representing the event state.
* Updated the `AccountService` to emit an event to the `bank-transactions` topic upon successful deposit or withdrawal operations.

### Step 4: The Consumer (Notification Service)
* Created a lightweight Spring Boot app mirroring the `TransactionEventDTO`.
* Configured JSON Type Mapping in `application.properties` to allow seamless deserialization from the producer's package to the consumer's package.
* Implemented a `@KafkaListener` to consume messages, evaluate business rules, and trigger actions asynchronously.

### Step 5: Independent Dockerization
To ensure true microservice isolation, standardizing deployments was necessary:
* Created specific Dockerfiles (`Dockerfile.bank` and `Dockerfile.notification`) utilizing multi-stage Maven builds.
* Orchestrated the entire ecosystem through `docker-compose.yml`, allowing both services to build and communicate over a shared internal Docker network.

---

## ✨ Features

- **Account Management**: Create, read, update, and close bank accounts.
- **Event-Driven Communication**: Asynchronous messaging using Apache Kafka.
- **Multi-Module Structure**: Clean separation of concerns using a parent Maven POM.
- **RESTful API**: Clean and intuitive REST endpoints.
- **API Documentation**: Interactive Swagger/OpenAPI documentation.
- **Data Persistence**: Spring Data JPA with database support.
- **Validation**: Jakarta Bean Validation for data integrity.
- **Exception Handling**: Comprehensive error handling and unified API responses.

## 💻 Technology Stack

- **Java 21**: Latest Java SDK
- **Spring Boot 3.3**: Application framework
- **Apache Kafka**: Message broker for event streaming
- **Spring Kafka**: Spring integration for Kafka producers and consumers
- **Spring MVC & Data JPA**: Web and data persistence layers
- **Jakarta EE**: Enterprise Java specifications
- **MS SQL Server**: Relational database
- **Swagger/OpenAPI 3**: API documentation
- **Docker & Docker Compose**: Containerization and orchestration

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.6+
- Docker & Docker Compose

### Running the Ecosystem (Docker Compose)

The easiest way to start the entire system (Database, Kafka Broker, Core Service, and Notification Service) is via Docker Compose.

Execute the following command in the root directory:
```bash
docker-compose up --build -d
```
Once the containers are up and running, the services will be available at:

    Bank Account Core API: http://localhost:11056

    Swagger UI (Core): http://localhost:11056/swagger-ui.html

    Notification Service (Logs): http://localhost:11057

Tip: You can monitor the asynchronous events by checking the notification service logs:
```bash

docker-compose logs -f notification
```
##🧪 Running Tests

To execute all unit and integration tests across all modules
```bash
mvn test
```
To build the project without running tests:
mvn clean install -DskipTests

##⚙️ Configuration

The applications can be configured via the application.properties files located in the src/main/resources folder of each respective module.
For production deployment, refer to the PROD_CONFIG_SETUP.md document for instructions on securing environment variables.
🙌 Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue.

    Fork the repository.

    Create your feature branch (git checkout -b feature/AmazingFeature).

    Commit your changes (git commit -m 'Add some AmazingFeature').

    Push to the branch (git push origin feature/AmazingFeature).

    Open a pull request.

##📄 License

This project is licensed under the MIT License - see the LICENSE.md file for details.
