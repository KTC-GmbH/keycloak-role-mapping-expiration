package de.ktc.keycloak.userroleexpiration.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoleExtensionsDto implements Serializable {

    private String roleId;
    private String name;
    private boolean active;
    private String expirationDate;
    private String resellerID;

    public UserRoleExtensionsDto() {
        // default constructor
    }

    public UserRoleExtensionsDto(String roleId, String name) {
        this.roleId = roleId;
        this.name = name;
        this.active = false;
    }

    public UserRoleExtensionsDto(String roleId, String name, boolean active, String expirationDate, String resellerID) {
        this.roleId = roleId;
        this.name = name;
        this.active = active;
        this.expirationDate = expirationDate;
        this.resellerID = resellerID;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getResellerID() {
        return resellerID;
    }

    public void setResellerID(String resellerID) {
        this.resellerID = resellerID;
    }

    @JsonIgnore
    public LocalDate getLocalDateExpirationDate() {
        if (this.expirationDate == null || this.expirationDate.isBlank()) {
            return null;
        }

        return LocalDate.parse(this.expirationDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
