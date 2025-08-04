# Build Stage : compiles app
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set working directory
WORKDIR /treso

# Copy wrapper and pom first for better caching
COPY mvnw mvnw.cmd pom.xml ./

# Copy the .mvn folder (needed by the wrapper to download Maven)
COPY .mvn .mvn

# Make maven wrapper executable and download dependecies (cached if pom.xml hasn't changed)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Then copy source and build
COPY src src

# Build the application (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# Run Stage : runs the app with a smaller image
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /treso

# Copy the built jar from build jar
COPY --from=builder /treso/target/*.jar treso.jar

# Run the application
ENTRYPOINT ["java", "-jar", "treso.jar"]