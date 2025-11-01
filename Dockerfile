# eContract KI - Render Production Dockerfile
# Multi-stage build for optimized image size

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="jb-x business solutions GmbH"
LABEL version="5.0.3"
LABEL description="eContract KI - Intelligent Contract Management System"

# Environment Variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SPRING_PROFILES_ACTIVE=production
ENV TZ=Europe/Berlin

# Create application directory
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 econtract && \
    adduser -D -u 1000 -G econtract econtract && \
    mkdir -p /app/logs /app/uploads && \
    chown -R econtract:econtract /app

# Copy built WAR from build stage
COPY --from=build --chown=econtract:econtract /build/target/econtract-ki.war /app/econtract-ki.war

# Switch to non-root user
USER econtract

# Expose port (Render will set PORT env variable)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/econtract/ || exit 1

# Start application with dynamic port from Render
CMD java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/econtract-ki.war
