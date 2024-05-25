FROM eclipse-temurin:17

ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ./target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 3000

# Command to run the Java application
CMD ["java", "-jar", "app.jar"]