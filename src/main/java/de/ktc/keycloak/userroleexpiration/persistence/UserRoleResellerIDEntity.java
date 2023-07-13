package de.ktc.keycloak.userroleexpiration.persistence;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_role_mapping_resellerid")
@NamedQueries({
        @NamedQuery(name = "findResellerIDByUser", query = "from UserRoleResellerIDEntity where userId = :userId"),
        @NamedQuery(name = "findResellerIDByUserAndRole", query = "from UserRoleResellerIDEntity where userId = :userId and roleId = :roleId")
})
public class UserRoleResellerIDEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "reseller_id")
    private String resellerID;

    public UserRoleResellerIDEntity() {
        // default public constructor
    }

    public UserRoleResellerIDEntity(String userId, String roleId, String resellerID) {
        this.userId = userId;
        this.roleId = roleId;
        this.resellerID = resellerID;
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

    public String getResellerID() {
        return resellerID;
    }

    public void setResellerID(String resellerID) {
        this.resellerID = resellerID;
    }
}
