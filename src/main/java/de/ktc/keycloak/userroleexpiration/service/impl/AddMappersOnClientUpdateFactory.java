package de.ktc.keycloak.userroleexpiration.service.impl;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class AddMappersOnClientUpdateFactory implements EventListenerProviderFactory {

    protected static final String ID = "add-mappers-on-client-update";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new AddMappersOnClientUpdate(session);
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
