# Use an image with Java 17
FROM adoptopenjdk:17-jdk-hotspot

WORKDIR /app

COPY target/prophius-assessment-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5060

ENTRYPOINT ["java", "-jar", "app.jar"]
