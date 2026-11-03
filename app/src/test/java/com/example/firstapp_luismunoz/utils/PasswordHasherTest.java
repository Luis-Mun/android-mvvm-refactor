package com.example.firstapp_luismunoz.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordHasherTest {

    @Test
    public void verify_acceptsCorrectPassword() {
        String stored = PasswordHasher.hash("correct horse");
        assertTrue(PasswordHasher.verify("correct horse", stored));
    }

    @Test
    public void verify_rejectsWrongPassword() {
        String stored = PasswordHasher.hash("correct horse");
        assertFalse(PasswordHasher.verify("wrong horse", stored));
    }

    @Test
    public void hash_isSaltedAndNeverContainsPlaintext() {
        String a = PasswordHasher.hash("same");
        String b = PasswordHasher.hash("same");
        assertNotEquals(a, b);
        assertFalse(a.contains("same"));
    }

    @Test
    public void verify_rejectsMalformedStoredValue() {
        assertFalse(PasswordHasher.verify("x", "plaintext"));
        assertFalse(PasswordHasher.verify("x", "pbkdf2$abc$!!$!!"));
        assertFalse(PasswordHasher.verify("x", null));
    }
}
