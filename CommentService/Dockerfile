FROM maven:3.9.9-eclipse-temurin-23 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
# этот слой будет «сквошен» в один
RUN mvn -B clean package -DskipTests

ENTRYPOINT ["top", "-b"]

FROM eclipse-temurin:23-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/target/CommentService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081
EXPOSE 50051

ENTRYPOINT ["java", "-jar", "app.jar"]