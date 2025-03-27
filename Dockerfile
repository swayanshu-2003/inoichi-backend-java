# Stage 1: Build the application
FROM maven:3.8.7-eclipse-temurin-17 AS build

# Set the working directory for the build stage
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml ./
COPY src ./src

# Build the application and package it as a JAR file
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime environment
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage to the runtime stage
COPY --from=build /app/target/Inoichi-Project-0.0.1-SNAPSHOT.jar /app/Inoichi Project-0.0.1-SNAPSHOT.jar

# Expose the port your app runs on (default 8080 for Spring Boot)
EXPOSE 8345

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/Inoichi Project-0.0.1-SNAPSHOT.jar"]