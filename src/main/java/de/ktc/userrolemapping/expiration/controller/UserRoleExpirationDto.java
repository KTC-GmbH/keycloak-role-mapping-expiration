package de.ktc.userrolemapping.expiration.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserRoleExpirationDto implements Serializable {

    private String roleId;
    private String name;
    private boolean active;
    private String expirationDate;

    public UserRoleExpirationDto() {
        // default constructor
    }

    public UserRoleExpirationDto(String roleId, String name) {
        this.roleId = roleId;
        this.name = name;
        this.active = false;
    }

    public UserRoleExpirationDto(String roleId, String name, boolean active, String expirationDate) {
        this.roleId = roleId;
        this.name = name;
        this.active = active;
        this.expirationDate = expirationDate;
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

    @JsonIgnore
    public LocalDate getLocalDateExpirationDate() {
        if (this.expirationDate == null || "".equals(this.expirationDate)) {
            return null;
        }

        return LocalDate.parse(this.expirationDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}


