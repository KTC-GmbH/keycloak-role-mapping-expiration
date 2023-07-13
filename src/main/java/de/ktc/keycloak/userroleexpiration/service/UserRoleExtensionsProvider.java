package de.ktc.keycloak.userroleexpiration.service;

import de.ktc.keycloak.userroleexpiration.controller.ClientDto;
import de.ktc.keycloak.userroleexpiration.controller.UserRoleExtensionsDto;
import org.keycloak.provider.Provider;

import java.util.List;

public interface UserRoleExtensionsProvider extends Provider {

    List<ClientDto> getAllByUser(String userId);

    UserRoleExtensionsDto save(String userId, UserRoleExtensionsDto userRoleExpirationDto);

    void scheduleCleanup();

}
