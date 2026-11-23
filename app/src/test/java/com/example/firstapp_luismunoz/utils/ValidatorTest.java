package com.example.firstapp_luismunoz.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ValidatorTest {

    @Test
    public void itemName_rules() {
        assertNull(Validator.validateItemName("Apples"));
        assertNotNull(Validator.validateItemName(null));
        assertNotNull(Validator.validateItemName("   "));
        assertNotNull(Validator.validateItemName(new String(new char[51]).replace('\0', 'a')));
    }

    @Test
    public void quantity_rules() {
        assertNull(Validator.validateQuantity("0"));
        assertNull(Validator.validateQuantity("999999"));
        assertNotNull(Validator.validateQuantity("1000000"));
        assertNotNull(Validator.validateQuantity("-1"));
        assertNotNull(Validator.validateQuantity("1.5"));
        assertNotNull(Validator.validateQuantity(""));
        assertNotNull(Validator.validateQuantity(null));
    }

    @Test
    public void username_rules() {
        assertNull(Validator.validateUsername("luis"));
        assertNotNull(Validator.validateUsername("ab"));
        assertNotNull(Validator.validateUsername(null));
    }

    @Test
    public void password_rules() {
        assertNull(Validator.validatePassword("secret"));
        assertNotNull(Validator.validatePassword("short"));
        assertNotNull(Validator.validatePassword(null));
    }

    @Test
    public void phoneNumber_acceptsCommonFormats() {
        assertNull(Validator.validatePhoneNumber("5551234567"));
        assertNull(Validator.validatePhoneNumber("(555) 123-4567"));
        assertNull(Validator.validatePhoneNumber("+1 555.123.4567"));
    }

    @Test
    public void phoneNumber_rejectsBadInput() {
        assertNotNull(Validator.validatePhoneNumber(null));
        assertNotNull(Validator.validatePhoneNumber("   "));
        assertNotNull(Validator.validatePhoneNumber("555-1234"));          // too short
        assertNotNull(Validator.validatePhoneNumber("1234567890123456"));  // too long
        assertNotNull(Validator.validatePhoneNumber("555-CALL-NOW"));      // letters
        assertNotNull(Validator.validatePhoneNumber("55+51234567"));       // + not at the start
    }

    @Test
    public void phoneNumber_normalizedForStorage() {
        org.junit.Assert.assertEquals("+15551234567", Validator.normalizePhoneNumber(" +1 (555) 123-4567 "));
        assertNull(Validator.normalizePhoneNumber(""));
    }
}
