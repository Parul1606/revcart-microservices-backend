FROM openjdk:21-slim

LABEL maintainer="your-email@example.com"
LABEL service="payment-service"

WORKDIR /app

# Copy built JAR file
COPY target/payment-service-*.jar app.jar

# Expose service port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8085/api/payments/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
