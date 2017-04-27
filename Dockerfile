FROM jboss/keycloak-mysql:3.0.0.Final

    
EXPOSE 8080

CMD ["-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
 
 

