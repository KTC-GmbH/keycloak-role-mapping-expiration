package de.ktc.keycloak.userroleexpiration.persistence;

import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.*;

@Entity
@Table(name = "user_client_has_auth")
@NamedQueries({
        @NamedQuery(name = "findByUserAndClient", query = "from UserClientHasAuthEntity where userId = :userId and clientId = :clientId")
})
public class UserClientHasAuthEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    public UserClientHasAuthEntity() {
        // default public constructor
    }

    public UserClientHasAuthEntity(String userId, String clientId) {
        this.id = KeycloakModelUtils.generateId();
        this.userId = userId;
        this.clientId = clientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
