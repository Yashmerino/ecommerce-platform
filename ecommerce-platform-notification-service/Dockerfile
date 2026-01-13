FROM maven:3.8.1-openjdk-17 AS build

WORKDIR /ecommerce-platform-notification-service

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src /ecommerce-platform-notification-service/src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /ecommerce-platform-notification-service

COPY --from=build /ecommerce-platform-notification-service/target/*.jar ecommerce-platform-notification-service.jar

EXPOSE 8083

RUN apt-get update && apt-get install -y curl

CMD ["java", "-jar", "ecommerce-platform-notification-service.jar"]
