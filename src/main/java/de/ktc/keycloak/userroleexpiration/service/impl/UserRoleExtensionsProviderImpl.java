package de.ktc.keycloak.userroleexpiration.service.impl;

import de.ktc.keycloak.userroleexpiration.controller.ClientDto;
import de.ktc.keycloak.userroleexpiration.controller.UserRoleExtensionsDto;
import de.ktc.keycloak.userroleexpiration.persistence.UserRoleExpirationEntity;
import de.ktc.keycloak.userroleexpiration.persistence.UserRoleResellerIDEntity;
import de.ktc.keycloak.userroleexpiration.service.UserRoleExtensionsProvider;
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

public class UserRoleExtensionsProviderImpl implements UserRoleExtensionsProvider {

    private final KeycloakSession session;

    public static final List<String> SYSTEM_CLIENTS = Arrays.asList("account", "account-console", "admin-cli", "broker", "master-realm", "security-admin-console");
    private static final long CLEANUP_INTERVAL = 1000L * 10 * 60;
    private static final String DEFAULT_RESELLER_ID = "KTC";

    public UserRoleExtensionsProviderImpl(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException("The service cannot accept a session without a realm in its context.");
        }
    }

    public void scheduleCleanup() {
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

        List<UserRoleResellerIDEntity> roleResellerIDs = getEntityManager()
                .createNamedQuery("findResellerIDByUser", UserRoleResellerIDEntity.class)
                .setParameter("userId", userId)
                .getResultList();

        return getRealm().getClientsStream()
                .filter(c -> !SYSTEM_CLIENTS.contains(c.getClientId()))
                .map(m -> toDto(m, user, roleExpirations, roleResellerIDs))
                .collect(Collectors.toList());
    }

    @Override
    public UserRoleExtensionsDto save(String userId, UserRoleExtensionsDto dto) {
        if (userId == null || dto == null) {
            throw new BadRequestException();
        }

        UserModel user = findUserByIdOrThrowException(userId);
        RoleModel role = findRoleByIdOrThrowException(dto.getRoleId());
        if (!user.hasRole(role)) {
            // todo: wenn ein user eine rolle entfernt bekommt, dann sollten auch die expiration date eintraege entfernt werden.
            throw new BadRequestException("the user does not have the role: " + dto.getRoleId());
        }

        UserRoleExpirationEntity storedExpirationEntity = saveExpirationEntity(userId, dto.getRoleId(), dto);
        UserRoleResellerIDEntity storedResellerIDEntity = saveResellerIDEntity(userId, dto.getRoleId(), dto);

        return new UserRoleExtensionsDto(
                storedExpirationEntity.getRoleId(),
                role.getName(),
                true,
                storedExpirationEntity.getExpirationDate() == null ? null : storedExpirationEntity.getExpirationDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                storedResellerIDEntity.getResellerID()
        );
    }

    private UserRoleResellerIDEntity saveResellerIDEntity(String userID, String roleID, UserRoleExtensionsDto dto) {
        String resellerID = dto.getResellerID() == null || dto.getResellerID().isBlank() ? DEFAULT_RESELLER_ID : dto.getResellerID(); // Default ID is KTC

        List<UserRoleResellerIDEntity> existingEntityList = getEntityManager()
                .createNamedQuery("findResellerIDByUserAndRole", UserRoleResellerIDEntity.class)
                .setParameter("userId", userID)
                .setParameter("roleId", roleID)
                .getResultList();

        UserRoleResellerIDEntity storedEntity;
        if (!existingEntityList.isEmpty()) {
            // Update existing entity
            UserRoleResellerIDEntity existingEntity = existingEntityList.get(0);
            existingEntity.setResellerID(resellerID);
            storedEntity = getEntityManager().merge(existingEntity);
        } else {
            // Create new entity
            storedEntity = new UserRoleResellerIDEntity(userID, dto.getRoleId(), resellerID);
            storedEntity.setId(KeycloakModelUtils.generateId());
            getEntityManager().persist(storedEntity);
        }

        return storedEntity;
    }

    private UserRoleExpirationEntity saveExpirationEntity(String userID, String roleID, UserRoleExtensionsDto dto) {
        List<UserRoleExpirationEntity> existingEntityList = getEntityManager()
                .createNamedQuery("findByUserAndRole", UserRoleExpirationEntity.class)
                .setParameter("userId", userID)
                .setParameter("roleId", roleID)
                .getResultList();

        UserRoleExpirationEntity storedEntity;
        if (!existingEntityList.isEmpty()) {
            // Update existing entity
            UserRoleExpirationEntity existingEntity = existingEntityList.get(0);
            existingEntity.setExpirationDate(dto.getLocalDateExpirationDate());
            storedEntity = getEntityManager().merge(existingEntity);
        } else {
            // Create new entity
            storedEntity = new UserRoleExpirationEntity(userID, dto.getRoleId(), dto.getLocalDateExpirationDate());
            storedEntity.setId(KeycloakModelUtils.generateId());
            getEntityManager().persist(storedEntity);
        }

        return storedEntity;
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

    private static ClientDto toDto(ClientModel m, UserModel u,
                                   List<UserRoleExpirationEntity> roleExpirations,
                                   List<UserRoleResellerIDEntity> roleResellerIDs) {
        if (m == null || u == null || roleExpirations == null) {
            return null;
        }

        List<String> userRoles = u.getClientRoleMappingsStream(m)
                .map(RoleModel::getId)
                .collect(Collectors.toList());

        List<UserRoleExtensionsDto> roles = m.getRolesStream()
                .map(r -> new UserRoleExtensionsDto(r.getId(), r.getName()))
                .peek(r -> r.setActive(userRoles.contains(r.getRoleId())))
                .peek(r -> {
                    LocalDate exp = expirationDateByRole(roleExpirations, r.getRoleId());
                    r.setExpirationDate(exp == null ? null : exp.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    r.setResellerID(resellerIDByRole(roleResellerIDs, r.getRoleId()));
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

    private static String resellerIDByRole(List<UserRoleResellerIDEntity> roleResellerIDs, String roleId) {
        Optional<UserRoleResellerIDEntity> o = roleResellerIDs.stream()
                .filter(r -> r.getRoleId().equals(roleId))
                .findAny();

        if (o.isEmpty()) {
            return null;
        }

        return o.get().getResellerID();
    }

}
