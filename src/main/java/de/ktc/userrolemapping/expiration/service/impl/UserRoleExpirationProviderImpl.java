package de.ktc.userrolemapping.expiration.service.impl;

import de.ktc.userrolemapping.expiration.controller.ClientDto;
import de.ktc.userrolemapping.expiration.controller.UserRoleExpirationDto;
import de.ktc.userrolemapping.expiration.persistence.UserRoleExpirationEntity;
import de.ktc.userrolemapping.expiration.service.UserRoleExpirationProvider;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.timer.TimerProvider;

import javax.persistence.EntityManager;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRoleExpirationProviderImpl implements UserRoleExpirationProvider {

    private final KeycloakSession session;

    private static final List<String> SYSTEM_CLIENTS = Arrays.asList("account", "account-console", "admin-cli", "broker", "master-realm", "security-admin-console");
    private static final long CLEANUP_INTERVAL = 1000L * 60 * 60; // every hour

    public UserRoleExpirationProviderImpl(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException("The service cannot accept a session without a realm in its context.");
        }

        TimerProvider timer = session.getProvider(TimerProvider.class);
        timer.scheduleTask(new UserRoleExpirationCleanupTask(), CLEANUP_INTERVAL, "user-role-expiration-cleanup");
    }

    @Override
    public List<ClientDto> getAllByUser(String userId) {
        UserModel user = findUserByIdOrThrowException(userId);

        List<UserRoleExpirationEntity> roleExpirations = getEntityManager()
                .createNamedQuery("findByUser", UserRoleExpirationEntity.class)
                .setParameter("userId", userId)
                .getResultList();

        return getRealm().getClientsStream()
                .filter(c -> !SYSTEM_CLIENTS.contains(c.getClientId()))
                .map(m -> toDto(m, user, roleExpirations))
                .collect(Collectors.toList());
    }

    @Override
    public UserRoleExpirationDto save(String userId, UserRoleExpirationDto userRoleExpirationDto) {
        if (userId == null || userRoleExpirationDto == null) {
            throw new BadRequestException();
        }

        UserModel user = findUserByIdOrThrowException(userId);
        RoleModel role = findRoleByIdOrThrowException(userRoleExpirationDto.getRoleId());
        if (!user.hasRole(role)) {
            throw new BadRequestException("the user does not have the role: " + userRoleExpirationDto.getRoleId());
        }

        List<UserRoleExpirationEntity> existingEntityList = getEntityManager()
                .createNamedQuery("findByUserAndRole", UserRoleExpirationEntity.class)
                .setParameter("userId", userId)
                .setParameter("roleId", userRoleExpirationDto.getRoleId())
                .getResultList();

        UserRoleExpirationEntity storedEntity;
        if (!existingEntityList.isEmpty()) {
            // Update existing entity
            UserRoleExpirationEntity existingEntity = existingEntityList.get(0);
            existingEntity.setExpirationDate(userRoleExpirationDto.getLocalDateExpirationDate());
            storedEntity = getEntityManager().merge(existingEntity);
        } else {
            // Create new entity
            storedEntity = new UserRoleExpirationEntity(userId, userRoleExpirationDto.getRoleId(), userRoleExpirationDto.getLocalDateExpirationDate());
            storedEntity.setId(KeycloakModelUtils.generateId());
            getEntityManager().persist(storedEntity);
        }

        return new UserRoleExpirationDto(storedEntity.getRoleId(), role.getName(), true, storedEntity.getExpirationDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public void close() {
        // do nothing
    }

    private UserModel findUserByIdOrThrowException(String userId) {
        UserModel user = session.users().getUserById(getRealm(), userId);
        if (user == null) {
            throw new NotFoundException("cannot find a user for the given id: " + userId);
        }
        return user;
    }

    private RoleModel findRoleByIdOrThrowException(String roleId) {
        RoleModel role = session.roles().getRoleById(getRealm(), roleId);
        if (role == null) {
            throw new NotFoundException("cannot find a role for the given id: " + roleId);
        }
        return role;
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    private static ClientDto toDto(ClientModel m, UserModel u, List<UserRoleExpirationEntity> roleExpirations) {
        if (m == null || u == null || roleExpirations == null) {
            return null;
        }

        List<String> userRoles = u.getClientRoleMappingsStream(m)
                .map(RoleModel::getId)
                .collect(Collectors.toList());

        List<UserRoleExpirationDto> roles = m.getRolesStream()
                .map(r -> new UserRoleExpirationDto(r.getId(), r.getName()))
                .peek(r -> r.setActive(userRoles.contains(r.getRoleId())))
                .peek(r -> {
                    LocalDate exp = expirationDateByRole(roleExpirations, r.getRoleId());
                    r.setExpirationDate(exp == null ? null : exp.format(DateTimeFormatter.ISO_LOCAL_DATE));
                })
                .collect(Collectors.toList());
        return new ClientDto(m.getClientId(), roles);
    }

    private static LocalDate expirationDateByRole(List<UserRoleExpirationEntity> roleExpirations, String roleId) {
        Optional<UserRoleExpirationEntity> o = roleExpirations.stream()
                .filter(r -> r.getRoleId().equals(roleId))
                .findAny();

        if (o.isEmpty()) {
            return null;
        }

        return o.get().getExpirationDate();
    }

}
