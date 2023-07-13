package de.ktc.keycloak.userroleexpiration.persistence;

import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.*;

@Entity
@Table(name = "role_expiration_duration")
@NamedQueries({
        @NamedQuery(name = "findExpirationDurationByRole", query = "from RoleExpirationDurationEntity where roleId = :roleId")
})
public class RoleExpirationDurationEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;


    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "expiration_duration_days")
    private Integer expirationDurationDays;

    public RoleExpirationDurationEntity() {
        // default public constructor
    }

    public RoleExpirationDurationEntity(String roleId, Integer expirationDurationDays) {
        this.id = KeycloakModelUtils.generateId();
        this.roleId = roleId;
        this.expirationDurationDays = expirationDurationDays;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Integer getExpirationDurationDays() {
        return expirationDurationDays;
    }

    public void setExpirationDurationDays(Integer expirationDurationDays) {
        this.expirationDurationDays = expirationDurationDays;
    }
}
