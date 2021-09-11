package de.ktc.userrolemapping.expiration.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRoleExpirationEntityProviderTest {

    private UserRoleExpirationEntityProvider underTest;

    @BeforeEach
    public void setup() {
        underTest = new UserRoleExpirationEntityProvider();
    }

    @Test
    public void testGetEntities() {
        // given

        // when
        List<Class<?>> result = underTest.getEntities();

        // then
        assertAll(
                () -> assertEquals(result.size(), 1),
                () -> assertEquals(result.get(0), UserRoleExpirationEntity.class)
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