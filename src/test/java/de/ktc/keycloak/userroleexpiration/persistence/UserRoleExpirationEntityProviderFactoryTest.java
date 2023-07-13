package de.ktc.keycloak.userroleexpiration.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleExpirationEntityProviderFactoryTest {

    private UserRoleExpirationEntityProviderFactory underTest;

    @BeforeEach
    public void setup() {
        underTest = new UserRoleExpirationEntityProviderFactory();
    }

    @Test
    void testCreate() {
        // given

        // when
        JpaEntityProvider result = underTest.create(null);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getClass(), UserRoleEntityExtensionsProvider.class)
        );
    }

    @Test
    void testGetId() {
        // given

        // when
        String result = underTest.getId();

        // then
        assertEquals("user-role-expiration-entity-provider", result);
    }

    @Test
    void testUnusedMethods() {
        // given

        // when
        underTest.init(null);
        underTest.postInit(null);
        underTest.close();

        // then
    }
}