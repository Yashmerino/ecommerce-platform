FROM maven:3.8.1-openjdk-17 AS build

WORKDIR /ecommerce-platform-payment-service

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src /ecommerce-platform-payment-service/src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /ecommerce-platform-payment-service

COPY --from=build /ecommerce-platform-payment-service/target/*.jar ecommerce-platform-payment-service.jar

EXPOSE 8082

RUN apt-get update && apt-get install -y curl

CMD ["java", "-jar", "ecommerce-platform-payment-service.jar"]
