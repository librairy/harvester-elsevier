FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD harvester-elsevier.one-jar.jar /app.jar
#RUN sh -c 'touch /app.jar'
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
ENTRYPOINT exec java $JAVA_OPTS -server -Done-jar.silent=true -jar /app.jar
