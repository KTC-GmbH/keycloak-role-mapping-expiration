package de.ktc.keycloak.userroleexpiration.controller;

import java.io.Serializable;
import java.util.List;

public class ClientDto implements Serializable {

    private final String clientId;
    private final List<UserRoleExtensionsDto> roles;

    public ClientDto(String clientId, List<UserRoleExtensionsDto> roles) {
        this.clientId = clientId;
        this.roles = roles;
    }

    public String getClientId() {
        return clientId;
    }

    public List<UserRoleExtensionsDto> getRoles() {
        return roles;
    }
}
