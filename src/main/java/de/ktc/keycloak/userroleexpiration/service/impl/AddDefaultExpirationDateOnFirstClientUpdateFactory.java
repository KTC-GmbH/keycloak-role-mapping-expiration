package de.ktc.keycloak.userroleexpiration.service.impl;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class AddDefaultExpirationDateOnFirstClientUpdateFactory implements EventListenerProviderFactory {

    protected static final String ID = "add-default-expiration-date-on-first-client-login";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new AddDefaultExpirationDateOnFirstClientUpdate(session);
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

    @Override
    public String getId() {
        return ID;
    }
}
