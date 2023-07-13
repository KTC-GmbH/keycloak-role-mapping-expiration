package de.ktc.keycloak.userroleexpiration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserRoleExpirationResourceProviderFactoryTest {

    private UserRoleExpirationResourceProviderFactory underTest;

    @BeforeEach
    public void setup() {
        underTest = new UserRoleExpirationResourceProviderFactory();
    }

    @Test
    public void testGetId() {
        // given

        // when
        String result = underTest.getId();

        // then
        assertEquals("user-role-expirations", result);
    }

    @Test
    public void testCreate() {
        // given
        KeycloakSession session = null;

        // when
        RealmResourceProvider result = underTest.create(session);

        // then
        assertNotNull(result);
    }

    @Test
    public void testUnusedMethods() {
        underTest.init(null);
        underTest.postInit(null);
        underTest.close();
    }

}