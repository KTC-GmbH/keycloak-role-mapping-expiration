package de.ktc.keycloak.userroleexpiration.service.impl;

import de.ktc.keycloak.userroleexpiration.persistence.UserRoleExpirationEntity;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.timer.ScheduledTask;

import javax.persistence.EntityManager;
import java.util.List;

public class UserRoleExpirationCleanupTask implements ScheduledTask {
    @Override
    public void run(KeycloakSession session) {
        EntityManager entityManager = getEntityManager(session);

        List<UserRoleExpirationEntity> expiredMappings = entityManager
                .createNamedQuery("findAllExpired", UserRoleExpirationEntity.class)
                .getResultList();

        expiredMappings.forEach(exp -> deleteMapping(entityManager, exp));
        expiredMappings.forEach(entityManager::remove);
    }

    private void deleteMapping(EntityManager entityManager, UserRoleExpirationEntity expirationEntity) {
        entityManager.createNativeQuery("delete from user_role_mapping where user_id = :userId and role_id = :roleId")
                .setParameter("userId", expirationEntity.getUserId())
                .setParameter("roleId", expirationEntity.getRoleId())
                .executeUpdate();
    }

    private EntityManager getEntityManager(KeycloakSession session) {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }
}
