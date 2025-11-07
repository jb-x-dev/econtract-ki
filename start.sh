#!/bin/sh

# Parse DATABASE_URL and transform to JDBC format
# Format: postgresql://user:password@host:port/database
# Target: jdbc:postgresql://host:port/database (credentials separate)

if [ -n "$DATABASE_URL" ]; then
  echo "Parsing DATABASE_URL..."
  
  # Extract components using sed/awk
  # Remove postgresql:// prefix
  DB_URL_NO_PREFIX=$(echo "$DATABASE_URL" | sed 's|postgresql://||')
  
  # Extract user:password part (before @)
  DB_CREDS=$(echo "$DB_URL_NO_PREFIX" | cut -d'@' -f1)
  DB_USER=$(echo "$DB_CREDS" | cut -d':' -f1)
  DB_PASS=$(echo "$DB_CREDS" | cut -d':' -f2)
  
  # Extract host:port/database part (after @)
  DB_HOST_PART=$(echo "$DB_URL_NO_PREFIX" | cut -d'@' -f2)
  
  # Build JDBC URL without credentials
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST_PART}"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASS}"
  
  echo "Database configuration:"
  echo "  URL: ${SPRING_DATASOURCE_URL}"
  echo "  User: ${SPRING_DATASOURCE_USERNAME}"
  echo "  Password: [HIDDEN]"
fi

# Start the Spring Boot application
exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.war
