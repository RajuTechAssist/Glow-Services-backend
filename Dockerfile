# build stage (Java 21)
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline -DskipTests
COPY src ./src
RUN mvn -B -DskipTests package

# runtime stage (Java 21)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
