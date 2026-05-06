# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/gestion_retours?useSSL=false&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/swagger-ui.html || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
