FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:23-jre-alpine AS runtime
WORKDIR /app

# Копируем собранный jar-файл из предыдущего контейнера
COPY --from=build /app/target/PostService-1.0-SNAPSHOT.jar /app/PostService.jar


EXPOSE 50050

CMD ["java", "-jar", "/app/PostService.jar"]