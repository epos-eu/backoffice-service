FROM amazoncorretto:17-alpine-jdk

ADD target/*.jar app.jar

RUN apk --no-cache add curl

ENTRYPOINT ["java","-Dlog4j.configurationFile=/etc/log4j2/log4j2.properties", "--add-opens=java.base/java.time=ALL-UNNAMED", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
