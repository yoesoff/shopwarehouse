# ==============================
# Stage 1 — Build the application
# ==============================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

# Install Liquibase CLI
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://github.com/liquibase/liquibase/releases/download/v4.27.0/liquibase-4.27.0.tar.gz && \
    tar -xzf liquibase-4.27.0.tar.gz && \
    mv liquibase /opt/liquibase && \
    ln -s /opt/liquibase/liquibase /usr/local/bin/liquibase

COPY src ./src

# Clear Liquibase checksums (ignore errors if config is missing)
RUN liquibase --changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml clearCheckSums || true

# Build WAR (skip tests for speed)
RUN mvn clean install -U -DskipTests

# ==============================
# Stage 2 — Run the application in Tomcat
# ==============================
FROM tomcat:10.1-jdk21-temurin AS runtime

WORKDIR /usr/local/tomcat/webapps

# Remove default ROOT app
RUN rm -rf ROOT

# Copy WAR from builder
COPY --from=builder /app/target/*.war ROOT.war

# Expose port
EXPOSE 8080

# Healthcheck (optional)
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Start Tomcat
CMD ["catalina.sh", "run"]
