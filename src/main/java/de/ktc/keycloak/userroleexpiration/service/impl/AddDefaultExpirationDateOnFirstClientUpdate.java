package de.ktc.keycloak.userroleexpiration.service.impl;

import de.ktc.keycloak.userroleexpiration.controller.RoleExpirationDurationDto;
import de.ktc.keycloak.userroleexpiration.controller.UserRoleExtensionsDto;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AddDefaultExpirationDateOnFirstClientUpdate implements EventListenerProvider {

    private static final List<EventType> TYPES = Arrays.asList(EventType.LOGIN, EventType.CODE_TO_TOKEN);

    private final RoleExpirationDurationsProviderImpl roleExpirationDurationsProvider;

    private final UserClientHasAuthProviderImpl userClientHasAuthProvider;

    private final UserRoleExtensionsProviderImpl userRoleExtensionsProvider;

    public AddDefaultExpirationDateOnFirstClientUpdate(KeycloakSession session) {
        this.roleExpirationDurationsProvider = new RoleExpirationDurationsProviderImpl(session);
        this.userClientHasAuthProvider = new UserClientHasAuthProviderImpl(session);
        this.userRoleExtensionsProvider = new UserRoleExtensionsProviderImpl(session);
    }

    @Override
    public void onEvent(Event event) {
        if (event == null || !TYPES.contains(event.getType())) {
            return;
        }

        String clientId = event.getClientId();
        String userId = event.getUserId();

        // do nothing if the user has already logged in to the client.
        if (userClientHasAuthProvider.hasLoggedInToClient(userId, clientId)) {
            return;
        }

        List<RoleExpirationDurationDto> defaultDurations = roleExpirationDurationsProvider.getAllForClient(clientId);
        for (RoleExpirationDurationDto d : defaultDurations) {
            if (d.getExpirationDurationDays() == null) {
                continue;
            }
            // String roleId, String name, boolean active, String expirationDate, String resellerID
            String expirationDate = LocalDate.now().plusDays(d.getExpirationDurationDays())
                            .format(DateTimeFormatter.ISO_DATE);
            UserRoleExtensionsDto dto = new UserRoleExtensionsDto(d.getRoleId(), null, true, expirationDate, null);
            userRoleExtensionsProvider.save(userId, dto);
        }

        userClientHasAuthProvider.logFirstLoginToClient(userId, clientId);
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }
}
