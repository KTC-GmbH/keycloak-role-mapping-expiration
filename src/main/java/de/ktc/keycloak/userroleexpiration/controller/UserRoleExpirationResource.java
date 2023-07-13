package de.ktc.keycloak.userroleexpiration.controller;

import de.ktc.keycloak.userroleexpiration.service.UserRoleExtensionsProvider;
import de.ktc.keycloak.userroleexpiration.service.impl.UserRoleExtensionsProviderImpl;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class UserRoleExpirationResource {

    private final UserRoleExtensionsProvider userRoleExtensionsProvider;

    public UserRoleExpirationResource(KeycloakSession session) {
        this.userRoleExtensionsProvider = new UserRoleExtensionsProviderImpl(session);
        userRoleExtensionsProvider.scheduleCleanup();
    }

    @GET
    @NoCache
    @Path("users/{userId}/user-role-expirations") // TODO: fix path
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClientDto> getRoleExpirationByUser(@PathParam("userId") final String userId) {
        return userRoleExtensionsProvider.getAllByUser(userId);
    }

    @POST
    @NoCache
    @Path("users/{userId}/user-role-expirations") // TODO: fix path
    @Produces(MediaType.APPLICATION_JSON)
    public UserRoleExtensionsDto saveRoleExpirationOfUser(@PathParam("userId") final String userId, final UserRoleExtensionsDto userRoleExpiration) {
        return userRoleExtensionsProvider.save(userId, userRoleExpiration);
    }

}
