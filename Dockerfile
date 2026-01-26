FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/BankAccountService-0.0.1.jar .
EXPOSE 11056
ENTRYPOINT ["java", "-jar", "BankAccountService-0.0.1.jar"]
