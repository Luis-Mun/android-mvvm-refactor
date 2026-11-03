package com.example.firstapp_luismunoz.utils;

/** Input validation rules. Each method returns an error message, or null when the input is valid. */
public final class Validator {

    public static final int MAX_ITEM_NAME_LENGTH = 50;
    public static final int MAX_QUANTITY = 999_999;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 30;
    public static final int MIN_PASSWORD_LENGTH = 6;

    private Validator() {}

    public static String validateItemName(String name) {
        if (name == null || name.trim().isEmpty()) return "Item name is required";
        if (name.trim().length() > MAX_ITEM_NAME_LENGTH) {
            return "Item name must be at most " + MAX_ITEM_NAME_LENGTH + " characters";
        }
        return null;
    }

    public static String validateQuantity(String quantity) {
        if (quantity == null || quantity.trim().isEmpty()) return "Quantity is required";
        try {
            int value = Integer.parseInt(quantity.trim());
            if (value < 0) return "Quantity cannot be negative";
            if (value > MAX_QUANTITY) return "Quantity must be at most " + MAX_QUANTITY;
            return null;
        } catch (NumberFormatException e) {
            return "Quantity must be a whole number";
        }
    }

    public static String validateUsername(String username) {
        if (username == null) return "Username is required";
        int length = username.trim().length();
        if (length < MIN_USERNAME_LENGTH || length > MAX_USERNAME_LENGTH) {
            return "Username must be " + MIN_USERNAME_LENGTH + "-" + MAX_USERNAME_LENGTH + " characters";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
        }
        return null;
    }
}
