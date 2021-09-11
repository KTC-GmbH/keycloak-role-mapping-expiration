FROM jboss/keycloak:15.0.2

ADD ./target/keycloak-user-role-mapping-expiration.jar /opt/jboss/keycloak/standalone/deployments/keycloak-user-role-mapping-expiration.jar
