FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM eclipse-temurin:23-jre
WORKDIR /app
# Copia el JAR de la etapa 'build' a la etapa actual
COPY --from=build /app/target/lab12026p.jar lab12026p.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","lab12026p.jar"]