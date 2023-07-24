FROM openjdk:17
VOLUME /tmp
COPY mdk data
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
