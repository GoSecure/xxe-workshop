FROM tomcat:8.5.14
#https://hub.docker.com/_/tomcat/

EXPOSE 8080

# Needed to render text in the SVG image
RUN apt-get install --no-install-recommends -y -q fonts-dejavu-core

COPY conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
