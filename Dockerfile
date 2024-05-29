FROM openjdk:20-jdk-slim
# eclipse-temurin:20

# Create app directory
WORKDIR /app

# Copy the JAR file
COPY ../../Desktop/Pehrs-mVM2/test%20med%20marcus/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar"]

