package de.ktc.keycloak.userroleexpiration.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRoleEntityExtensionsProviderTest {

    private UserRoleEntityExtensionsProvider underTest;

    @BeforeEach
    public void setup() {
        underTest = new UserRoleEntityExtensionsProvider();
    }

    @Test
    public void testGetEntities() {
        // given

        // when
        List<Class<?>> result = underTest.getEntities();

        // then
        assertAll(
                () -> assertEquals(4, result.size()),
                () -> assertEquals(UserRoleExpirationEntity.class, result.get(0))
        );
    }

    @Test
    public void testGetChangelogLocation() {
        // given

        // when
        String result = underTest.getChangelogLocation();

        // then
        assertEquals(result, "META-INF/changelog.xml");
    }

    @Test
    public void testGetFactoryId() {
        // given

        // when
        String result = underTest.getFactoryId();

        // then
        assertEquals(result, "user-role-expiration-entity-provider");
    }

    @Test
    public void testUnusedMethods() {
        // given

        // when
        underTest.close();

        // then
    }

}