package de.ktc.userrolemapping.expiration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakUriInfo;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.timer.TimerProvider;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import java.lang.reflect.Field;
import java.net.URI;

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
    public void testGetUserRoleExpirationResourcesWithoutAnAuth() throws Exception {
        // given

        // when
        authField.set(underTest, null);
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());

        // then
    }

    @Test
    public void testGetUserRoleExpirationResourcesWithoutAToken() throws Exception {
        // given

        // when
        authField.set(underTest, auth);
        assertThrows(NotAuthorizedException.class, () -> underTest.getUserRoleExpirationResources());

        // then
    }

    @Test
    public void testGetUserRoleExpirationResourcesWithValidToken() throws Exception {
        // given

        // when
        authField.set(underTest, auth);
        when(auth.getToken()).thenReturn(mock(AccessToken.class));
        when(session.getProvider(TimerProvider.class)).thenReturn(mock(TimerProvider.class));
        assertDoesNotThrow(() -> underTest.getUserRoleExpirationResources());

        // then
    }

}