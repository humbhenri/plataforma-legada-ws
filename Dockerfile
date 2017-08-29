FROM tomcat:8
MAINTAINER Humberto Pinheiro <humbhenri@gmail.com>
ADD target/cobol-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/cobol.war
