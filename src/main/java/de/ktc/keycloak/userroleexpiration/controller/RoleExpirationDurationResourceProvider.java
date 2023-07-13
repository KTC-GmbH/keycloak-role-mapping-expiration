package de.ktc.keycloak.userroleexpiration.controller;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class RoleExpirationDurationResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public RoleExpirationDurationResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new RoleExpirationDurationResource(session); // TODO: auth interceptor
    }

    @Override
    public void close() {
        // nothing to do
    }

}
