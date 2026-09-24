# Stage 1: Build the application using Maven and Java 25
FROM maven:3.9.16-eclipse-temurin-25 AS builder

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy application source code
COPY src ./src

# Build the application without running tests
RUN mvn clean package -DskipTests


# Stage 2: Create the runtime image
FROM eclipse-temurin:25-jre AS event-service

WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Document the application port
EXPOSE 8081

# Start Event Service
ENTRYPOINT ["java", "-jar", "app.jar"]