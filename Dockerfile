FROM maven:3.8.3-openjdk-17 as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/*-fat.jar /usr/local/lib/server.jar

ENTRYPOINT exec java -jar /usr/local/lib/server.jar