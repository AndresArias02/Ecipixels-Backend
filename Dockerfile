FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ADD ./target/EciPixels-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
