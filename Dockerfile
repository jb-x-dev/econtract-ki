# eContract KI - Dockerfile with URL transformation
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

# Copy startup script
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

# Expose port (Render will set PORT env variable)
EXPOSE 8080

# Use startup script instead of direct java command
CMD ["/app/start.sh"]
