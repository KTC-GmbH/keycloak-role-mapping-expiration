package de.ktc.userrolemapping.expiration.controller;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class UserRoleExpirationResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public UserRoleExpirationResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new AuthInterceptor(session);
    }

    @Override
    public void close() {
        // nothing to do
    }

}
