# Stage 1: builder - Build the app and extract the app layers
FROM maven:3.9.16-amazoncorretto-25-alpine AS builder
WORKDIR /builder
ARG ARTIFACTS_BUILD_DIRECTORY=./build

# Download dependencies
# COPY creates a cacheable layer: every time its file changes, it rebuilds the cache
COPY pom.xml .
RUN mvn dependency:go-offline -B -Pdockerizable

# Copy source code
COPY src ./src

# Package application
ARG JAR_FILE=bareo-backend.jar
RUN mvn package -DskipTests -Pdockerizable,!testing -B
COPY target/*.jar ${JAR_FILE}

# Extract the layered JAR content
RUN java -Djarmode=tools -jar target/${JAR_FILE} extract --layers --destination extracted

# ---

# Stage 2 final image - Use a JRE to run the app
FROM amazoncorretto:25-alpine
WORKDIR /application

ARG APP_VERSION=0.0.1-SNAPSHOT

# Metadata labels (OpenContainers)
LABEL org.opencontainers.image.title="Bareo Backend"
LABEL org.opencontainers.image.version="${APP_VERSION}"

# Create a non-root user to run app securely
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the JAR layers in the correct order, so the cache is optimized
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

# Set owner and user of files
RUN chown -R appuser:appgroup /application
USER appuser

# Run app using the SpringBoot launcher
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]