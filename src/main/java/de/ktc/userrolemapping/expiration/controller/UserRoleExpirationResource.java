package de.ktc.userrolemapping.expiration.controller;

import de.ktc.userrolemapping.expiration.service.UserRoleExpirationProvider;
import de.ktc.userrolemapping.expiration.service.impl.UserRoleExpirationProviderImpl;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class UserRoleExpirationResource {

    private final UserRoleExpirationProvider userRoleExpirationProvider;

    public UserRoleExpirationResource(KeycloakSession session) {
        this.userRoleExpirationProvider = new UserRoleExpirationProviderImpl(session);
    }

    @GET
    @NoCache
    @Path("users/{userId}/user-role-expirations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClientDto> getRoleExpirationByUser(@PathParam("userId") final String userId) {
        return userRoleExpirationProvider.getAllByUser(userId);
    }

    @POST
    @NoCache
    @Path("users/{userId}/user-role-expirations")
    @Produces(MediaType.APPLICATION_JSON)
    public UserRoleExpirationDto saveRoleExpirationOfUser(@PathParam("userId") final String userId, final UserRoleExpirationDto userRoleExpiration) {
        return userRoleExpirationProvider.save(userId, userRoleExpiration);
    }

}
