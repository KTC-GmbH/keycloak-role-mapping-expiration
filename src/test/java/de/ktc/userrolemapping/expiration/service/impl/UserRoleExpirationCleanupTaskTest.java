package de.ktc.userrolemapping.expiration.service.impl;

import de.ktc.userrolemapping.expiration.persistence.UserRoleExpirationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleExpirationCleanupTaskTest {

    private UserRoleExpirationCleanupTask underTest;
    private KeycloakSession session;
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        underTest = new UserRoleExpirationCleanupTask();

        session = mock(KeycloakSession.class);
        entityManager = mock(EntityManager.class);
        JpaConnectionProvider jpaConnectionProvider = mock(JpaConnectionProvider.class);

        when(session.getProvider(JpaConnectionProvider.class))
                .thenReturn(jpaConnectionProvider);
        when(jpaConnectionProvider.getEntityManager())
                .thenReturn(entityManager);
    }

    @Test
    public void testRunWithoutExpiratedMappings() {
        // given
        TypedQuery<UserRoleExpirationEntity> typedQuery = mock(TypedQuery.class);
        when(entityManager.createNamedQuery("findAllExpired", UserRoleExpirationEntity.class))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList())
                .thenReturn(Collections.emptyList());

        // when
        underTest.run(session);

        // then
        verify(entityManager, never()).createNativeQuery(any());
    }

    @Test
    public void testRunWithExpiratedMappings() {
        // given
        UserRoleExpirationEntity e1 = new UserRoleExpirationEntity("1", "2", LocalDate.now());
        UserRoleExpirationEntity e2 = new UserRoleExpirationEntity("3", "4", LocalDate.now());


        TypedQuery<UserRoleExpirationEntity> typedQuery = mock(TypedQuery.class);
        when(entityManager.createNamedQuery("findAllExpired", UserRoleExpirationEntity.class))
                .thenReturn(typedQuery);
        Query nativeQuery = mock(Query.class);
        when(entityManager.createNativeQuery(any()))
                .thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), anyString())).thenReturn(nativeQuery);
        when(typedQuery.getResultList())
                .thenReturn(Arrays.asList(e1, e2));

        // when
        underTest.run(session);

        // then
        verify(entityManager, times(2)).createNativeQuery(any());
    }

}