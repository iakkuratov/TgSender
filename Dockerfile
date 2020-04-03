#
# Build stage
#
FROM maven:3.5-jdk-8 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

#
# Package stage
#
FROM gcr.io/distroless/java
COPY --from=build /usr/src/app/target/TgSender-1.1-jar-with-dependencies.jar /usr/app/tgsender.jar
ENV TGPORT="8080"
ENV TGNAME=""
ENV TGTOKEN=""
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/tgsender.jar"]