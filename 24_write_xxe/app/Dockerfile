FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp

RUN sh -c 'touch /app.jar && mkdir -p /data/templates && mkdir /secret'

COPY build/libs/books-0.0.1-SNAPSHOT.jar app.jar
COPY build/resources/main/data/doc_book_preview.xsl /data/templates/doc_book_preview.xsl
COPY build/resources/main/data/doc_book_sample.xml /data/templates/doc_book_sample.xml
COPY flag/flag.pdf /flag/flag.pdf

EXPOSE 8024

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-DresourceDir=/data/templates/","-jar","/app.jar"]
