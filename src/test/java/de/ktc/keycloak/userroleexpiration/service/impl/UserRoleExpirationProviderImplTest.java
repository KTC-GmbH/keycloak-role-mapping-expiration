package de.ktc.keycloak.userroleexpiration.service.impl;

import de.ktc.keycloak.userroleexpiration.controller.ClientDto;
import de.ktc.keycloak.userroleexpiration.controller.UserRoleExtensionsDto;
import de.ktc.keycloak.userroleexpiration.persistence.UserRoleExpirationEntity;
import de.ktc.keycloak.userroleexpiration.persistence.UserRoleResellerIDEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.*;
import org.keycloak.timer.TimerProvider;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleExpirationProviderImplTest {

    private UserRoleExtensionsProviderImpl underTest;
    private KeycloakSession session;
    private EntityManager entityManager;
    private TimerProvider timer;

    @BeforeEach
    void setup() {
        session = mock(KeycloakSession.class);
        entityManager = mock(EntityManager.class);

        JpaConnectionProvider jpaConnectionProvider = mock(JpaConnectionProvider.class);
        lenient().when(session.getProvider(JpaConnectionProvider.class)).thenReturn(jpaConnectionProvider);
        lenient().when(jpaConnectionProvider.getEntityManager()).thenReturn(entityManager);
        KeycloakContext keycloakContext = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(keycloakContext);

        RealmModel realm = mock(RealmModel.class);
        when(keycloakContext.getRealm()).thenReturn(realm);

        ClientModel c1 = mock(ClientModel.class);
        c1.setClientId("client01");
        ClientModel c2 = mock(ClientModel.class);
        c2.setClientId("client02");

        lenient().when(realm.getClientsStream()).thenReturn(Stream.of(c1, c2));

        RoleModel r1 = mock(RoleModel.class);
        lenient().when(r1.getId()).thenReturn("1");
        lenient().when(r1.getName()).thenReturn("role01");
        RoleModel r2 = mock(RoleModel.class);
        lenient().when(r2.getId()).thenReturn("2");
        lenient().when(r2.getName()).thenReturn("role02");
        lenient().when(c1.getRolesStream()).thenReturn(Stream.of(r1, r2));

        timer = mock(TimerProvider.class);

        underTest = new UserRoleExtensionsProviderImpl(session);
    }

    @Test
    void testStartCleanupJob() {
        // given

        // when
        when(session.getProvider(TimerProvider.class)).thenReturn(timer);
        underTest.scheduleCleanup();

        // then
        verify(timer, times(1)).scheduleTask(any(UserRoleExpirationCleanupTask.class), anyLong(), anyString());
    }

    @Test
    void testGetAllByUserWithoutAUserId() {
        // given
        String userId = null;
        UserProvider userProvider = mock(UserProvider.class);

        // when
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(any(RealmModel.class), any()))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> underTest.getAllByUser(userId));

        // then
    }

    @Test
    void testGetAllByUserWithAUnknownUserId() {
        // given
        String userId = "1";
        UserProvider userProvider = mock(UserProvider.class);

        // when
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(any(RealmModel.class), eq(userId)))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> underTest.getAllByUser(userId));

        // then
    }

    @Test
    void testGetAllByUserWithoutUserRoleExpirations() {
        // given
        String userId = "1";
        UserProvider userProvider = mock(UserProvider.class);
        UserModel user = mock(UserModel.class);
        TypedQuery<UserRoleExpirationEntity> typedQueryFindByUser = mock(TypedQuery.class);
        TypedQuery<UserRoleResellerIDEntity> typedQueryFindByResellerIDByUser = mock(TypedQuery.class);

        // when
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(any(RealmModel.class), eq(userId)))
                .thenReturn(user);
        // findByUser
        when(entityManager.createNamedQuery("findByUser", UserRoleExpirationEntity.class))
                .thenReturn(typedQueryFindByUser);
        when(typedQueryFindByUser.setParameter(anyString(), anyString()))
                .thenReturn(typedQueryFindByUser);
        when(typedQueryFindByUser.getResultList())
                .thenReturn(Collections.emptyList());
        // findResellerIDByUser
        when(entityManager.createNamedQuery("findResellerIDByUser", UserRoleResellerIDEntity.class))
                .thenReturn(typedQueryFindByResellerIDByUser);
        when(typedQueryFindByResellerIDByUser.setParameter(anyString(), anyString()))
                .thenReturn(typedQueryFindByResellerIDByUser);
        when(typedQueryFindByResellerIDByUser.getResultList())
                .thenReturn(Collections.emptyList());

        List<ClientDto> result = underTest.getAllByUser(userId);

        // then
        assertEquals(2, result.size());
    }

    @Test
    void testSaveWithoutInputParameters() {
        // given
        String userId = null;
        UserRoleExtensionsDto dto = null;

        // when
        assertThrows(BadRequestException.class, () -> underTest.save(userId, dto));
    }

    @Test
    void testSaveWithARoleThatIsNotMappedToTheUser() {
        // given
        String userId = "1";
        UserRoleExtensionsDto dto = new UserRoleExtensionsDto("2", "role01", true, "2021-09-10", "KTC");

        UserProvider userProvider = mock(UserProvider.class);
        UserModel user = mock(UserModel.class);
        RoleProvider roleProvider = mock(RoleProvider.class);
        RoleModel role = mock(RoleModel.class);

        // when
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(any(RealmModel.class), eq(userId))).thenReturn(user);
        when(session.roles()).thenReturn(roleProvider);
        when(roleProvider.getRoleById(underTest.getRealm(), dto.getRoleId())).thenReturn(role);

        assertThrows(BadRequestException.class, () -> underTest.save(userId, dto));
    }

    @Test
    void testSaveANonExistingMapping() {
        // given
        String userId = "1";
        UserRoleExtensionsDto dto = new UserRoleExtensionsDto("2", "role01", true, "2021-09-10", "KTC");

        UserProvider userProvider = mock(UserProvider.class);
        UserModel user = mock(UserModel.class);
        RoleProvider roleProvider = mock(RoleProvider.class);
        RoleModel role = mock(RoleModel.class);
        when(role.getName()).thenReturn(dto.getName());

        TypedQuery<UserRoleExpirationEntity> typedQuery = mock(TypedQuery.class);
        TypedQuery<UserRoleResellerIDEntity> typedQueryFindResellerIDByUserAndRole = mock(TypedQuery.class);

        // when
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(any(RealmModel.class), eq(userId))).thenReturn(user);
        when(session.roles()).thenReturn(roleProvider);
        when(roleProvider.getRoleById(underTest.getRealm(), dto.getRoleId())).thenReturn(role);

        when(user.hasRole(role)).thenReturn(true);

        when(entityManager.createNamedQuery("findByUserAndRole", UserRoleExpirationEntity.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyString())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());
        // findResellerIDByUser
        when(entityManager.createNamedQuery("findResellerIDByUserAndRole", UserRoleResellerIDEntity.class))
                .thenReturn(typedQueryFindResellerIDByUserAndRole);
        when(typedQueryFindResellerIDByUserAndRole.setParameter(anyString(), anyString()))
                .thenReturn(typedQueryFindResellerIDByUserAndRole);
        when(typedQueryFindResellerIDByUserAndRole.getResultList())
                .thenReturn(Collections.emptyList());

        UserRoleExtensionsDto result = underTest.save(userId, dto);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(dto.getRoleId(), result.getRoleId()),
                () -> assertEquals(dto.getName(), result.getName()),
                () -> assertTrue(result.isActive()),
                () -> assertEquals(dto.getResellerID(), result.getResellerID()),
                () -> assertEquals("2021-09-10", result.getExpirationDate())
        );

        verify(entityManager, times(2)).persist(any());
        verify(entityManager, times(0)).merge(any());
    }

    @Test
    void testSaveAnExistingMapping() {
        // given
        String userId = "1";
        UserRoleExtensionsDto dto = new UserRoleExtensionsDto("2", "role01", true, "2021-09-10", "KTC");
        UserRoleExpirationEntity entity = new UserRoleExpirationEntity(userId, dto.getRoleId(), LocalDate.parse(dto.getExpirationDate()));
        UserRoleResellerIDEntity resellerEntity = new UserRoleResellerIDEntity(userId, dto.getRoleId(), dto.getResellerID());

        UserProvider userProvider = mock(UserProvider.class);
        UserModel user = mock(UserModel.class);
        RoleProvider roleProvider = mock(RoleProvider.class);
        RoleModel role = mock(RoleModel.class);
        when(role.getName()).thenReturn(dto.getName());

        TypedQuery<UserRoleExpirationEntity> typedQuery = mock(TypedQuery.class);
        TypedQuery<UserRoleResellerIDEntity> typedQueryFindResellerIDByUserAndRole = mock(TypedQuery.class);

        // when
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(any(RealmModel.class), eq(userId))).thenReturn(user);
        when(session.roles()).thenReturn(roleProvider);
        when(roleProvider.getRoleById(underTest.getRealm(), dto.getRoleId())).thenReturn(role);

        when(user.hasRole(role)).thenReturn(true);

        when(entityManager.createNamedQuery("findByUserAndRole", UserRoleExpirationEntity.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyString())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(entity));
        // findResellerIDByUser
        when(entityManager.createNamedQuery("findResellerIDByUserAndRole", UserRoleResellerIDEntity.class))
                .thenReturn(typedQueryFindResellerIDByUserAndRole);
        when(typedQueryFindResellerIDByUserAndRole.setParameter(anyString(), anyString()))
                .thenReturn(typedQueryFindResellerIDByUserAndRole);
        when(typedQueryFindResellerIDByUserAndRole.getResultList())
                .thenReturn(Collections.singletonList(resellerEntity));

        when(entityManager.merge(entity)).thenReturn(entity);
        when(entityManager.merge(resellerEntity)).thenReturn(resellerEntity);

        UserRoleExtensionsDto result = underTest.save(userId, dto);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(dto.getRoleId(), result.getRoleId()),
                () -> assertEquals(dto.getName(), result.getName()),
                () -> assertTrue(result.isActive()),
                () -> assertEquals(dto.getResellerID(), result.getResellerID()),
                () -> assertEquals("2021-09-10", result.getExpirationDate())
        );

        verify(entityManager, times(0)).persist(any());
        verify(entityManager, times(2)).merge(any());
    }

    @Test
    void testUnusedMethods() {
        underTest.close();
    }

}