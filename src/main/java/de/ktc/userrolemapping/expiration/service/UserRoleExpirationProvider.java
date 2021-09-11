package de.ktc.userrolemapping.expiration.service;

import de.ktc.userrolemapping.expiration.controller.ClientDto;
import de.ktc.userrolemapping.expiration.controller.UserRoleExpirationDto;
import org.keycloak.provider.Provider;

import java.util.List;

public interface UserRoleExpirationProvider extends Provider {

    List<ClientDto> getAllByUser(String userId);

    UserRoleExpirationDto save(String userId, UserRoleExpirationDto userRoleExpirationDto);

}
