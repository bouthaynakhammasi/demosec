# Stage 1: Build the Spring Boot application
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies for faster subsequent builds
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the JAR
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true


# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar file from the build stage (naming must match pom.xml: <artifactId>-<version>.jar)
COPY --from=build /app/target/demosec-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8081 as defined in application.properties
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

