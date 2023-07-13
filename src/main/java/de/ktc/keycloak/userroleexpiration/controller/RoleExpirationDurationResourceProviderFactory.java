package de.ktc.keycloak.userroleexpiration.controller;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class RoleExpirationDurationResourceProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "role-expiration-durations";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new RoleExpirationDurationResourceProvider(session);
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