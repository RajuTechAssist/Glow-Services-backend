# build stage
From maven:3.9.11-eclipse-temurin-21 As build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# runtime stage
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /app/target/glow-services-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT [ "java", "-jar", "app.jar" ]   


