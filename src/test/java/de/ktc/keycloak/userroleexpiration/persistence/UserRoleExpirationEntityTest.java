package de.ktc.keycloak.userroleexpiration.persistence;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleExpirationEntityTest {

    @Test
    public void testGetterAndSetter() {
        // given
        UserRoleExpirationEntity underTest = new UserRoleExpirationEntity();

        // when
        underTest.setId("1");
        underTest.setUserId("2");
        underTest.setRoleId("3");
        underTest.setExpirationDate(LocalDate.of(2021, 9, 10));

        // then
        assertAll(
                () -> assertEquals(underTest.getId(), "1"),
                () -> assertEquals(underTest.getUserId(), "2"),
                () -> assertEquals(underTest.getRoleId(), "3"),
                () -> assertEquals(underTest.getExpirationDate(), LocalDate.of(2021, 9, 10))
        );
    }

    @Test
    public void testCustomConstructor() {
        // given
        UserRoleExpirationEntity underTest = new UserRoleExpirationEntity("2", "3", LocalDate.of(2021, 9, 10));

        // when

        // then
        // then
        assertAll(
                () -> assertNull(underTest.getId()),
                () -> assertEquals(underTest.getUserId(), "2"),
                () -> assertEquals(underTest.getRoleId(), "3"),
                () -> assertEquals(underTest.getExpirationDate(), LocalDate.of(2021, 9, 10))
        );
    }

}