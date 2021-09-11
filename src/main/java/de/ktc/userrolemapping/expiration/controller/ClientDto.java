package de.ktc.userrolemapping.expiration.controller;

import java.io.Serializable;
import java.util.List;

public class ClientDto implements Serializable {

    private final String clientId;
    List<UserRoleExpirationDto> roles;

    public ClientDto(String clientId, List<UserRoleExpirationDto> roles) {
        this.clientId = clientId;
        this.roles = roles;
    }

    public String getClientId() {
        return clientId;
    }

    public List<UserRoleExpirationDto> getRoles() {
        return roles;
    }
}
