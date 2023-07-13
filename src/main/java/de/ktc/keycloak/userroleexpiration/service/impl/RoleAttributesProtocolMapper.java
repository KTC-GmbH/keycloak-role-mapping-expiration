package de.ktc.keycloak.userroleexpiration.service.impl;

import de.ktc.keycloak.userroleexpiration.controller.ClientDto;
import de.ktc.keycloak.userroleexpiration.persistence.UserRoleExpirationEntity;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.ktc.keycloak.userroleexpiration.service.impl.UserRoleExtensionsProviderImpl.SYSTEM_CLIENTS;

/**
 * Mapper, um zusätzliche Informationen über die Clients und Rollen des Realms im AccessToken darzustellen.<br />
 * <br />
 * Aktuell werden folgende Funktionalitäten bereitgestellt:<br />
 * <ul>
 *     <li>Die zusätzlichen Rollen-Attribute (via: {@link RoleModel#getAttributes()})</li>
 *     <li>Der Gültigkeitszeitraum der Rollenzuordnung (via: {@link UserRoleExpirationEntity#getExpirationDate()})</li>
 * </ul>
 * <br />
 * Hierbei werden nur {@link ClientModel}s betrachtet, die nicht Teil der initialen Clients von Keycloak sind.
 * Es werden also nur zusätzliche Rollen-Attribute von {@link ClientModel}s angezeigt, die für eine "reale"
 * Anwendung ist und daher auch Lizenzen braucht.
 *
 * @author Fabian Frontzek
 */
public class RoleAttributesProtocolMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper {

    public static final String PROVIDER_ID = "oidc-role-attributes-mapper";

    // Name des zusätzlichen Attributes im AccessToken.
    private static final String CLAIM_KEY = "resource_role-attributes";
    // Name, der in der UI angezeigt wird.
    private static final String DISPLAY_NAME = "KTC :: Lizenz-Eigenschaften in Lizenz";
    // Text, der beim Klick auf das '?'-Symbol angezeigt wird.
    private static final String HELP_TEXT = "Mapper, um zusätzliche Informationen über die Clients und Rollen des Realms im AccessToken darzustellen.";
    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    @Override
    public AccessToken transformAccessToken(final AccessToken token,
                                            final ProtocolMapperModel mappingModel,
                                            final KeycloakSession session,
                                            final UserSessionModel userSession,
                                            final ClientSessionContext clientSessionCtx) {
        // Holt die Werte aller Keycloak-Extensions für diesen Benutzer. Aktuell wird hieraus
        // nur das Gültigkeitsdatum verwendet.
        List<ClientDto> extensions = getClientExtensions(session, userSession.getUser().getId());

        Map<String, List<RoleAttributesDto>> roleAttributes = session.clients()
                .getClientsStream(userSession.getRealm())
                // Herausfiltern der irrelevanten Clients. Also Clients, die initial durch Keycloak mitgeliefert werden.
                .filter(c -> !SYSTEM_CLIENTS.contains(c.getClientId()))
                // Erzeugt eine Map aus der ClientID und dessen zusätzlichen Attributen.
                .collect(Collectors.toMap(ClientModel::getClientId, c -> mapRolesOfClient(c, extensions)));

        token.getOtherClaims().put(CLAIM_KEY, roleAttributes);
        return token;
    }

    // Erzeugt für einen Client eine Liste von angereicherten Rollen-Eigenschaften.
    // Aktuell werden die Rollen-Attribute und das Gültigkeitsdatum zurückgegeben.
    private static List<RoleAttributesDto> mapRolesOfClient(ClientModel client, List<ClientDto> extensions) {
        List<RoleAttributesDto> attributes = client.getRolesStream()
                .map(RoleAttributesDto::new)
                .collect(Collectors.toList());

        // Schaut, ob es für den Client zusätzliche Informationen gibt.
        Optional<ClientDto> e = findByClient(extensions, client);
        if (e.isEmpty()) {
            return attributes;
        }

        ClientDto c = e.get();
        for (RoleAttributesDto ra : attributes) {
            // Schaut, ob es für die Rolle zusätzliche Informationen gibt.
            c.getRoles().stream()
                    .filter(r -> r.getName().equals(ra.getName()))
                    .findAny()
                    .ifPresent(userRoleExtensionsDto -> ra.setExpirationDate(userRoleExtensionsDto.getExpirationDate()));
        }

        return attributes;
    }

    // Schaut, ob es für den gegebenen Client in der Liste der zusätzlichen Eigenschaften
    // einen Eintrag gibt und gibt diesen zurück.
    private static Optional<ClientDto> findByClient(List<ClientDto> extensions, ClientModel client) {
        if (extensions == null || client == null) {
            return Optional.empty();
        }

        return extensions.stream()
                .filter(e -> e.getClientId().equals(client.getClientId()))
                .findAny();
    }

    // Lädt die zusätzlichen Client-Eigenschaften aus der Datenbank und gibt sie zurück.
    private static List<ClientDto> getClientExtensions(KeycloakSession session, String userId) {
        UserRoleExtensionsProviderImpl provider = new UserRoleExtensionsProviderImpl(session);
        return provider.getAllByUser(userId);
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return DISPLAY_NAME;
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}
