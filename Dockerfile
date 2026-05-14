# Stage 1: Build avec Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:resolve
COPY . .
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime optimisé
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="GestionRetours Team"
LABEL version="1.0.0"
LABEL description="API Backend - Gestion des Retours Produits (GCP)"

WORKDIR /app
RUN apk add --no-cache curl bash && \
    addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser

# Copy JAR from builder
COPY --from=builder --chown=appuser:appuser /build/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables - defaults for GCP
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"
ENV SPRING_PROFILES_ACTIVE=gcp
ENV SPRING_APPLICATION_NAME=gestion-retours-backend
ENV SERVER_PORT=8080
ENV SERVER_SERVLET_CONTEXT_PATH=/api

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/v3/api-docs || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
