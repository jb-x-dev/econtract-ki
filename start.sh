#!/bin/sh

# Transform DATABASE_URL from postgresql:// to jdbc:postgresql://
if [ -n "$DATABASE_URL" ]; then
  # Remove postgresql:// prefix and add jdbc:postgresql:// prefix
  export SPRING_DATASOURCE_URL="jdbc:${DATABASE_URL}"
  echo "Transformed DATABASE_URL to JDBC format"
  echo "SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}"
fi

# Start the Spring Boot application
exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.war
