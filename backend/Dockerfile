# backend/Dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY ./githubfiles /app

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/githubfiles-0.0.1-SNAPSHOT.jar"]