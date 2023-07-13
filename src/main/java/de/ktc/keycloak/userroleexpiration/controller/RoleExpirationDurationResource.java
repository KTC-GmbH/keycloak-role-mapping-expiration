package de.ktc.keycloak.userroleexpiration.controller;

import de.ktc.keycloak.userroleexpiration.service.impl.RoleExpirationDurationsProviderImpl;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class RoleExpirationDurationResource {

    private final RoleExpirationDurationsProviderImpl roleExpirationDurationsProvider;

    public RoleExpirationDurationResource(KeycloakSession keycloakSession) {
        roleExpirationDurationsProvider = new RoleExpirationDurationsProviderImpl(keycloakSession);
    }

    @GET
    @NoCache
    @Path("clients/{clientID}/role-expiration-durations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RoleExpirationDurationDto> getAllForClient(@PathParam("clientID") String clientID) {
        return roleExpirationDurationsProvider.getAllForClient(clientID);
    }

    @GET
    @NoCache
    @Path("roles/{roleID}/role-expiration-duration")
    @Produces(MediaType.APPLICATION_JSON)
    public RoleExpirationDurationDto getRoleExpirationDurationForRole(@PathParam("roleID") String roleID) {
        return roleExpirationDurationsProvider.getRoleExpirationDurationForRole(roleID);
    }

    @PUT
    @NoCache
    @Path("roles/{roleID}/role-expiration-duration")
    @Produces(MediaType.APPLICATION_JSON)
    public RoleExpirationDurationDto saveRoleExpirationDuration(@PathParam("roleID") String roleID, RoleExpirationDurationDto roleExpirationDurationDto) {
        return roleExpirationDurationsProvider.saveRoleExpirationDuration(roleID, roleExpirationDurationDto);
    }

}
