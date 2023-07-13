package de.ktc.keycloak.userroleexpiration.service.impl;

import de.ktc.keycloak.userroleexpiration.persistence.UserClientHasAuthEntity;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

import javax.persistence.EntityManager;

public class UserClientHasAuthProviderImpl implements Provider {

    private final KeycloakSession session;

    public UserClientHasAuthProviderImpl(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException("The service cannot accept a session without a realm in its context.");
        }
    }

    public boolean hasLoggedInToClient(String userID, String clientID) {
        if (userID == null || clientID == null) {
            return true;
        }

        return !getEntityManager()
                .createNamedQuery("findByUserAndClient")
                .setParameter("userId", userID)
                .setParameter("clientId", clientID)
                .getResultList().isEmpty();
    }

    public void logFirstLoginToClient(String userID, String clientID) {
        // Do nothing if the user has already logged in to the client.
        if (userID == null || clientID == null || hasLoggedInToClient(userID, clientID)) {
            return;
        }

        getEntityManager().persist(new UserClientHasAuthEntity(userID, clientID));
    }

    @Override
    public void close() {
        // do nothing
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

}
