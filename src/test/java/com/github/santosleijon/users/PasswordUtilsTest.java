package com.github.santosleijon.users;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordUtilsTest {

    @Test
    void passwordShouldBeAbleToBeHashedAndThenVerified() {
        var plainPassword = "my-secret-password";

        var hashedPassword = PasswordUtils.hashPassword(plainPassword);
        assertThat(hashedPassword).isNotBlank();
        assertThat(hashedPassword).isNotEqualTo(plainPassword);

        assertThat(PasswordUtils.verifyPassword(plainPassword, hashedPassword)).isTrue();

        System.out.println(hashedPassword);
    }
}