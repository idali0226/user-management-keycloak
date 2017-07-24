# Keycloak

## Setup

Dependencies on the host
- Maven
- openjdk-8-jdk
- npm & nodejs
- bower
- Ember

## Startup

### To start dina-user-management api in wildfly:

Go to directory dina-user-management/target

  java -Dswarm.http.port=8181 -jar user-management-api-swarm.jar

Set up initial data in Keyloak: y on first startup, n on susequent startups
Keycloak url: http://localhost:8080/auth
Stop gracefully: Ctrl + c
Kill non-gracefully stopped processes before re-launching:

  ps 
  kill -9 [process id]
 
### To start frontend

Go to directory user-management-ui

  ember s
