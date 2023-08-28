FROM openjdk:17
VOLUME /tmp
COPY mdk-min data/sites
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app.jar"]
