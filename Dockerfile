# eContract KI - Dockerfile with URL transformation
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application with retry logic (Maven Central can have temporary issues)
# Retry up to 3 times with 10 second delays
RUN for i in 1 2 3; do \
    echo "Maven build attempt $i/3..." && \
    mvn clean package -DskipTests && break || \
    (echo "Maven build failed, waiting 10 seconds before retry..." && sleep 10); \
    done && \
    if [ ! -f target/*.war ]; then \
        echo "Maven build failed after 3 attempts" && exit 1; \
    fi

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
