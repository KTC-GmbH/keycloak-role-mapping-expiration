package de.ktc.userrolemapping.expiration.controller;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class UserRoleExpirationResourceProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "user-role-expirations";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new UserRoleExpirationResourceProvider(session);
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