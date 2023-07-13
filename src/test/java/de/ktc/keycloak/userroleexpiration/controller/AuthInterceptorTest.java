package de.ktc.keycloak.userroleexpiration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.timer.TimerProvider;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    private AuthInterceptor underTest;
    private KeycloakSession session;
    private AuthenticationManager.AuthResult auth;
    private Field authField;

    @BeforeEach
    public void setup() throws Exception {
        session = mock(KeycloakSession.class);
        auth = mock(AuthenticationManager.AuthResult.class);

        authField = AuthInterceptor.class.getDeclaredField("auth");
        authField.setAccessible(true);

        HttpHeaders headers = mock(HttpHeaders.class);
        when(session.getContext()).thenReturn(mock(KeycloakContext.class));
        when(session.getContext().getRequestHeaders()).thenReturn(headers);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedHashMap<>());
        when(session.getContext().getRealm()).thenReturn(mock(RealmModel.class));
        when(session.getContext().getUri()).thenReturn(mock(KeycloakUriInfo.class));
        when(session.getContext().getConnection()).thenReturn(mock(ClientConnection.class));

        when(session.getContext().getUri().getBaseUri()).thenReturn(URI.create("http://localhost:8080"));
        when(session.getContext().getRealm().getName()).thenReturn("realm");

        underTest = new AuthInterceptor(session);
    }

    @Test
    void testGetUserRoleExpirationResourcesWithoutAnAuth() throws Exception {
        // given

        // when
        authField.set(underTest, null);

        // then
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());
    }

    @Test
    void testGetUserRoleExpirationResourcesWithoutAToken() throws Exception {
        // given

        // when
        authField.set(underTest, auth);

        // then
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());
    }

    @Test
    void testGetUserRoleExpirationResourcesWithoutResourceAccess() throws Exception {
        // given
        UserSessionModel userSession = mock(UserSessionModel.class);
        when(auth.getSession()).thenReturn(userSession);
        when(userSession.getRealm()).thenReturn(mock(RealmModel.class));
        when(userSession.getRealm().getName()).thenReturn("test");
        UserModel user = mock(UserModel.class);
        when(userSession.getUser()).thenReturn(user);
        when(user.getRealmRoleMappingsStream()).thenReturn(Stream.empty());

        // when
        authField.set(underTest, auth);
        when(auth.getToken()).thenReturn(mock(AccessToken.class));
        when(auth.getToken().getResourceAccess("test-realm")).thenReturn(null);

        // then
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());
    }

    @Test
    void testGetUserRoleExpirationResourcesWithoutRoles() throws Exception {
        // given
        Set<String> userRoles = null;

        UserSessionModel userSession = mock(UserSessionModel.class);
        when(auth.getSession()).thenReturn(userSession);
        when(userSession.getRealm()).thenReturn(mock(RealmModel.class));
        when(userSession.getRealm().getName()).thenReturn("test");

        AccessToken.Access realmMgmtAccess = mock(AccessToken.Access.class);
        when(realmMgmtAccess.getRoles()).thenReturn(userRoles);

        UserModel user = mock(UserModel.class);
        when(userSession.getUser()).thenReturn(user);
        when(user.getRealmRoleMappingsStream()).thenReturn(Stream.empty());

        // when
        authField.set(underTest, auth);
        when(auth.getToken()).thenReturn(mock(AccessToken.class));
        when(auth.getToken().getResourceAccess("test-realm")).thenReturn(realmMgmtAccess);

        // then
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());
    }

    @Test
    void testGetUserRoleExpirationResourcesWithEmptyRoles() throws Exception {
        // given
        Set<String> userRoles = new HashSet<>();

        UserSessionModel userSession = mock(UserSessionModel.class);
        when(auth.getSession()).thenReturn(userSession);
        when(userSession.getRealm()).thenReturn(mock(RealmModel.class));
        when(userSession.getRealm().getName()).thenReturn("test");

        AccessToken.Access realmMgmtAccess = mock(AccessToken.Access.class);
        when(realmMgmtAccess.getRoles()).thenReturn(userRoles);

        UserModel user = mock(UserModel.class);
        when(userSession.getUser()).thenReturn(user);
        when(user.getRealmRoleMappingsStream()).thenReturn(Stream.empty());

        // when
        authField.set(underTest, auth);
        when(auth.getToken()).thenReturn(mock(AccessToken.class));
        when(auth.getToken().getResourceAccess("test-realm")).thenReturn(realmMgmtAccess);

        // then
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());
    }

    @Test
    void testGetUserRoleExpirationResourcesWithoutTheRequiredRoles() throws Exception {
        // given
        Set<String> userRoles = new HashSet<>();
        userRoles.add("not-required");
        userRoles.add("wer-die-rolle-hat-ist-dumm");

        UserSessionModel userSession = mock(UserSessionModel.class);
        when(auth.getSession()).thenReturn(userSession);
        when(userSession.getRealm()).thenReturn(mock(RealmModel.class));
        when(userSession.getRealm().getName()).thenReturn("test");

        AccessToken.Access realmMgmtAccess = mock(AccessToken.Access.class);
        when(realmMgmtAccess.getRoles()).thenReturn(userRoles);

        UserModel user = mock(UserModel.class);
        when(userSession.getUser()).thenReturn(user);
        when(user.getRealmRoleMappingsStream()).thenReturn(Stream.empty());

        // when
        authField.set(underTest, auth);
        when(auth.getToken()).thenReturn(mock(AccessToken.class));
        when(auth.getToken().getResourceAccess("test-realm")).thenReturn(realmMgmtAccess);

        // then
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());
    }

    @Test
    void testGetUserRoleExpirationResourcesWithOnlyOneRequiredRole() throws Exception {
        // given
        Set<String> userRoles = new HashSet<>();
        userRoles.add("not-required");
        userRoles.add("wer-die-rolle-hat-ist-dumm");
        userRoles.add("manage-users");

        UserSessionModel userSession = mock(UserSessionModel.class);
        when(auth.getSession()).thenReturn(userSession);
        when(userSession.getRealm()).thenReturn(mock(RealmModel.class));
        when(userSession.getRealm().getName()).thenReturn("test");

        AccessToken.Access realmMgmtAccess = mock(AccessToken.Access.class);
        when(realmMgmtAccess.getRoles()).thenReturn(userRoles);

        UserModel user = mock(UserModel.class);
        when(userSession.getUser()).thenReturn(user);
        when(user.getRealmRoleMappingsStream()).thenReturn(Stream.empty());

        // when
        authField.set(underTest, auth);
        when(auth.getToken()).thenReturn(mock(AccessToken.class));
        when(auth.getToken().getResourceAccess("test-realm")).thenReturn(realmMgmtAccess);

        // then
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());
    }

    @Test
    void testGetUserRoleExpirationResourcesWithAllRequiredRoles() throws Exception {
        // given
        Set<String> userRoles = new HashSet<>();
        userRoles.add("not-required");
        userRoles.add("wer-die-rolle-hat-ist-dumm");
        userRoles.add("manage-users");
        userRoles.add("manage-clients");

        UserSessionModel userSession = mock(UserSessionModel.class);
        when(auth.getSession()).thenReturn(userSession);
        when(userSession.getRealm()).thenReturn(mock(RealmModel.class));
        when(userSession.getRealm().getName()).thenReturn("test");

        AccessToken.Access realmMgmtAccess = mock(AccessToken.Access.class);
        when(realmMgmtAccess.getRoles()).thenReturn(userRoles);

        UserModel user = mock(UserModel.class);
        when(userSession.getUser()).thenReturn(user);
        when(user.getRealmRoleMappingsStream()).thenReturn(Stream.empty());

        // when
        authField.set(underTest, auth);
        when(session.getProvider(TimerProvider.class)).thenReturn(mock(TimerProvider.class));
        when(auth.getToken()).thenReturn(mock(AccessToken.class));
        when(auth.getToken().getResourceAccess("test-realm")).thenReturn(realmMgmtAccess);

        // then
        assertDoesNotThrow(() -> underTest.getUserRoleExpirationResources());
    }

}