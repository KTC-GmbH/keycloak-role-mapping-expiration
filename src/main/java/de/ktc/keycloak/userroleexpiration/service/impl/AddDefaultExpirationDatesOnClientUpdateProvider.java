package de.ktc.keycloak.userroleexpiration.service.impl;

import org.keycloak.models.ClientModel;
import org.keycloak.provider.ProviderEvent;
import org.keycloak.provider.ProviderEventListener;

public class AddDefaultExpirationDatesOnClientUpdateProvider implements ProviderEventListener {

    // This update event is also called when creating a new client.
    @Override
    public void onEvent(ProviderEvent event) {
        if (!(event instanceof ClientModel.ClientUpdatedEvent)) {
            return;
        }

        // TODO: If a new client role is added as default role:
        // TODO:    - Add it to all users that have already logged in to the client
    }
}
