FROM store/oracle/serverjre:8

# Set the working directory to /app
WORKDIR /app

# Add fat jar to app
ADD target/docker-1.0-SNAPSHOT-fat.jar /app

# Make port 80 available to the world outside this container
EXPOSE 8080

# Run vert.x when container starts
CMD java -jar docker-1.0-SNAPSHOT-fat.jar