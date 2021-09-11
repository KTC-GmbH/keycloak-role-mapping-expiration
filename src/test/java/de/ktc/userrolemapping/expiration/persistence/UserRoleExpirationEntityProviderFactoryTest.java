package de.ktc.userrolemapping.expiration.persistence;

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
    public void testCreate() {
        // given

        // when
        JpaEntityProvider result = underTest.create(null);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getClass(), UserRoleExpirationEntityProvider.class)
        );
    }

    @Test
    public void testGetId() {
        // given

        // when
        String result = underTest.getId();

        // then
        assertEquals(result, "user-role-expiration-entity-provider");
    }

    @Test
    public void testUnusedMethods() {
        // given

        // when
        underTest.init(null);
        underTest.postInit(null);
        underTest.close();

        // then
    }
}