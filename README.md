# Keycloak

## Setup

Dependencies on the host:

- Docker
- Git
- Maven
- openjdk-8-jdk
- npm & nodejs
- bower
- Ember

Clone or from this repository.

(Re)Build and start services:

    docker-compose up -d fs
    docker-compose up -d db
    docker-compose up sso

Setup parameters:

- keycloak_auth_url = http://localhost:8080/auth
- mail_username = dina@mail.dina-web.net
- mail_password = [PASSWORD]
- mail_host = mail.dina-web.net
- mail_port = 587

Add first admin user into Keycloak:

    docker exec usermanagementkeycloak_sso_1 keycloak/bin/add-user-keycloak.sh -u admin -p [PASSWORD]

## Startup

### Start / stop SSO container

Go to directory `dina-user-management`

    docker-compose start sso
    docker-compose stop sso

### Start API in wildfly:

Go to directory `dina-user-management/target`

    java -Dswarm.http.port=8181 -jar user-management-api-swarm.jar

Set up initial data in Keycloak: `y` on first startup, `n` on subsequent startups

Keycloak url: `http://localhost:8080/auth`

Stop gracefully: `Ctrl + c`

Kill non-gracefully stopped processes before re-launching:

    ps
    kill -9 [PROCESS ID]
 
### Start frontend

Go to directory `user-management-ui`

    ember s
