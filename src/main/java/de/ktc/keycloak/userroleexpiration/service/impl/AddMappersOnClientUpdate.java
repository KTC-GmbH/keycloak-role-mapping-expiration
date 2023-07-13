package de.ktc.keycloak.userroleexpiration.service.impl;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.RealmModel;

public class AddMappersOnClientUpdate implements EventListenerProvider {

    private final KeycloakSession session;

    public AddMappersOnClientUpdate(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        // do nothing
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (event == null || !ResourceType.CLIENT.equals(event.getResourceType())) {
            return;
        }

        String clientId = event.getResourcePath().split("/")[1];
        ClientModel client = session.clients().getClientById(getRealm(), clientId);

        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName("KTC :: Lizenz-Eigenschaften in Lizenz Mapper");
        mapper.setProtocol("openid-connect");
        mapper.setProtocolMapper(RoleAttributesProtocolMapper.PROVIDER_ID);
        client.addProtocolMapper(mapper);
    }

    @Override
    public void close() {
        // do nothing
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

}
