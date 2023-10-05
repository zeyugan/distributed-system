
# Use a base image with the appropriate Java version (e.g., OpenJDK 11)
FROM openjdk:20

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Java application JAR file into the container
COPY ./out/artifacts/distributed_system_jar/distributed-system.jar /app/

# Command to run your Java application
CMD ["java", "-jar", "distributed-system.jar"]
