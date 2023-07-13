package de.ktc.keycloak.userroleexpiration.persistence;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_role_mapping_expiration")
@NamedQueries({
        @NamedQuery(name = "findAllExpired", query = "from UserRoleExpirationEntity where date(expirationDate) < current_date"),
        @NamedQuery(name = "findByUser", query = "from UserRoleExpirationEntity where userId = :userId"),
        @NamedQuery(name = "findByUserAndRole", query = "from UserRoleExpirationEntity where userId = :userId and roleId = :roleId")
})
public class UserRoleExpirationEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    public UserRoleExpirationEntity() {
        // default public constructor
    }

    public UserRoleExpirationEntity(String userId, String roleId, LocalDate expirationDate) {
        this.userId = userId;
        this.roleId = roleId;
        this.expirationDate = expirationDate;
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

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleId() {
        return roleId;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
