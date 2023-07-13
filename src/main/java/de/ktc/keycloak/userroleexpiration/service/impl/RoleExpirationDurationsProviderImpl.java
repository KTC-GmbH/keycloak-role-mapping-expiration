package de.ktc.keycloak.userroleexpiration.service.impl;

import de.ktc.keycloak.userroleexpiration.controller.RoleExpirationDurationDto;
import de.ktc.keycloak.userroleexpiration.persistence.RoleExpirationDurationEntity;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class RoleExpirationDurationsProviderImpl implements Provider {

    private final KeycloakSession session;

    public RoleExpirationDurationsProviderImpl(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException("The service cannot accept a session without a realm in its context.");
        }
    }

    public List<RoleExpirationDurationDto> getAllForClient(String clientID) {
        ClientModel client = findByIdOrThrowException(clientID);

        return client.getRolesStream()
                .map(r -> getRoleExpirationDurationForRole(r.getId()))
                .collect(Collectors.toList());
    }

    public RoleExpirationDurationDto getRoleExpirationDurationForRole(String roleId) {
        List<RoleExpirationDurationEntity> result = getEntityManager()
                .createNamedQuery("findExpirationDurationByRole", RoleExpirationDurationEntity.class)
                .setParameter("roleId", roleId)
                .getResultList();

        if (result.isEmpty()) {
            return new RoleExpirationDurationDto(roleId, null);
        }

        return new RoleExpirationDurationDto(roleId, result.get(0).getExpirationDurationDays());
    }

    public RoleExpirationDurationDto saveRoleExpirationDuration(String roleId, RoleExpirationDurationDto roleExpirationDurationDto) {
        List<RoleExpirationDurationEntity> result = getEntityManager()
                .createNamedQuery("findExpirationDurationByRole", RoleExpirationDurationEntity.class)
                .setParameter("roleId", roleId)
                .getResultList();

        RoleExpirationDurationEntity e;
        if (result.isEmpty()) {
            e = new RoleExpirationDurationEntity(roleId, roleExpirationDurationDto.getExpirationDurationDays());
            getEntityManager().persist(e);
        } else {
            e = result.get(0);
            getEntityManager().merge(e);
        }

        return new RoleExpirationDurationDto(e.getRoleId(), e.getExpirationDurationDays());
    }

    @Override
    public void close() {
        // do nothing
    }

    private ClientModel findByIdOrThrowException(String clientID) {
        ClientModel client = session.clients().getClientByClientId(getRealm(), clientID);
        if (client == null) {
            throw new NotFoundException("cannot find a client for the given id: " + clientID);
        }

        return client;
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

}
