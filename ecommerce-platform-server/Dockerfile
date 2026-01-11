FROM maven:3.8.1-openjdk-17 AS build

WORKDIR /ecommerce-platform-server

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src /ecommerce-platform-server/src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /ecommerce-platform-server

COPY --from=build /ecommerce-platform-server/target/*.jar ecommerce-platform-server.jar

EXPOSE 8081

RUN apt-get update && apt-get install -y curl

CMD ["java", "-jar", "ecommerce-platform-server.jar"]
