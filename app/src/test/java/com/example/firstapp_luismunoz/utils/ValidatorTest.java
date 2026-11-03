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
}
