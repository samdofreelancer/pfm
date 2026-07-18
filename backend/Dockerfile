FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and pom files
COPY pom.xml .
COPY pfm-common/pom.xml pfm-common/
COPY pfm-domain/pom.xml pfm-domain/
COPY pfm-application/pom.xml pfm-application/
COPY pfm-infrastructure/pom.xml pfm-infrastructure/
COPY pfm-api/pom.xml pfm-api/
COPY pfm-bootstrap/pom.xml pfm-bootstrap/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY pfm-common/src pfm-common/src
COPY pfm-domain/src pfm-domain/src
COPY pfm-application/src pfm-application/src
COPY pfm-infrastructure/src pfm-infrastructure/src
COPY pfm-api/src pfm-api/src
COPY pfm-bootstrap/src pfm-bootstrap/src

# Build application
RUN mvn clean package -DskipTests -pl pfm-bootstrap -am

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=builder /app/pfm-bootstrap/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Duser.timezone=Asia/Ho_Chi_Minh", "-jar", "app.jar"]