package de.ktc.keycloak.userroleexpiration.controller;

import java.io.Serializable;

public class RoleExpirationDurationDto implements Serializable {

    private String roleId;
    private Integer expirationDurationDays;

    public RoleExpirationDurationDto() {
        // default public constructor
    }

    public RoleExpirationDurationDto(String roleId, Integer expirationDurationDays) {
        this.roleId = roleId;
        this.expirationDurationDays = expirationDurationDays;
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
