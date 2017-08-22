FROM openjdk:8-jdk-alpine

COPY target/user-management-api-swarm.jar /usr/src/myapp/ 
COPY project-initdata.yml /usr/src/myapp/
 
WORKDIR /usr/src/myapp

RUN apk add --no-cache tini openssl bash 

RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    -O /wait-for-it.sh \
    && chmod +x /wait-for-it.sh

ENTRYPOINT ["/sbin/tini", "--", "/wait-for-it.sh", "sso:8080", "-t", "60", "--"]


USER root
EXPOSE 8181
 
CMD ["java", "-jar", "user-management-api-swarm.jar",  "-Sinitdata" ]

 






 