FROM frolvlad/alpine-java:jre8-slim
VOLUME /tmp

COPY build/libs/rssviewer-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
