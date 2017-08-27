FROM tomcat:8
MAINTAINER Humberto Pinheiro <humbhenri@gmail.com>
ADD . /tmp
WORKDIR /tmp
RUN apt update \
    && apt install default-jdk maven -y \
    && mvn clean package \
    && cp target/cobol-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/cobol.war
