# eContract KI - Simplified Dockerfile for Render
# Single-stage build for reliability

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Runtime with OpenJDK 17
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built WAR file
COPY --from=build /app/target/*.war app.war

# Expose port (Render will set PORT env variable)
EXPOSE 8080

# Run the application with dynamic port
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Xmx512m -Xms256m -jar app.war"]
