
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app


COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/ms-vidasalud-bff-*.jar app.jar

USER spring


EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
