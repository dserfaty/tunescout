FROM openjdk:23
MAINTAINER 1023dev.com
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]