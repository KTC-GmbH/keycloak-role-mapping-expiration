package de.ktc.userrolemapping.expiration.controller;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;

public class AuthInterceptor {

    private final KeycloakSession session;
    private final AuthenticationManager.AuthResult auth;

    public AuthInterceptor(KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    @Path("api")
    public UserRoleExpirationResource getUserRoleExpirationResources() {
        checkAuth();
        return new UserRoleExpirationResource(session);
    }

    private void checkAuth() {
        if (auth == null || auth.getToken() == null) {
            throw new NotAuthorizedException("Bearer");
        }
    }
}
