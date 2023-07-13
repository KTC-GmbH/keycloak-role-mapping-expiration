package de.ktc.keycloak.userroleexpiration.persistence;

import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UserRoleExpirationEntityProviderFactory implements JpaEntityProviderFactory {

    protected static final String ID = "user-role-expiration-entity-provider";

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new UserRoleEntityExtensionsProvider();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Config.Scope config) {
        // do nothing
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }

}
