package de.ktc.keycloak.userroleexpiration.controller;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.Set;

public class AuthInterceptor {

    private static final Logger LOGGER = Logger.getLogger(AuthInterceptor.class);

    private static final String[] REQUIRED_ROLES = new String[]{"manage-clients", "manage-users"};

    private final KeycloakSession session;
    private final AuthenticationManager.AuthResult auth;


    public AuthInterceptor(KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    @Path("api")
    public UserRoleExpirationResource getUserRoleExpirationResources() {
        checkAuth(REQUIRED_ROLES);
        return new UserRoleExpirationResource(session);
    }

    // Überprüft, ob der angemeldete Benutzer die benötigten Rollen
    // für den Zugriff der Extensions besitzt. Falls nicht, wird ein
    // Error geworfen.
    private void checkAuth(String... requiredRoles) {
        // Überprüft, ob der Benutzer angemeldet ist.
        if (auth == null || auth.getToken() == null) {
            throw new NotAuthorizedException("Bearer");
        }

        // Admins können alles
        boolean isAdmin = auth.getSession().getUser().getRealmRoleMappingsStream()
                .anyMatch(r -> r.getName().equals("admin"));
        if (isAdmin) {
            return;
        }

        AccessToken token = auth.getToken();

        // Standard Namensschema von Keycloak: <name>-realm
        String realmMgmtClientID = auth.getSession().getRealm().getName() + "-realm";
        AccessToken.Access realmMgmtAccess = token.getResourceAccess(realmMgmtClientID);

        if (realmMgmtAccess == null) {
            LOGGER.info("Missing resource access to: '" + realmMgmtClientID + "'");
            throw new NotAuthorizedException("Missing resource access");
        }

        Set<String> realmMgmtRoles = realmMgmtAccess.getRoles();
        if (realmMgmtRoles == null) {
            LOGGER.warn("Missing required role(s): " + Arrays.toString(requiredRoles));
            throw new NotAuthorizedException("Missing required role(s): " + Arrays.toString(requiredRoles));
        }

        // Überprüfe, ob der Benutzer alle benötigten Rollen besitzt.
        for (String r : requiredRoles) {
            if (!realmMgmtRoles.contains(r)) {
                throw new NotAuthorizedException("Missing required role: '" + r + "'");
            }
        }
    }
}
