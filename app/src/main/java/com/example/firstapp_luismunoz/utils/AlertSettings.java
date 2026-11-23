package com.example.firstapp_luismunoz.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores the phone number that receives low-stock SMS alerts. The original app hard-coded this
 * number in two places in MainActivity; now the user sets it from the menu and it is kept in
 * private SharedPreferences, so it never appears in source control.
 */
public final class AlertSettings {

    private static final String PREFS_NAME = "alert_settings";
    private static final String KEY_PHONE_NUMBER = "alert_phone_number";

    private final SharedPreferences prefs;

    public AlertSettings(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** @return the saved number, or null if the user has not set one yet. */
    public String getPhoneNumber() {
        return prefs.getString(KEY_PHONE_NUMBER, null);
    }

    /** Saves a number that has already passed {@link Validator#validatePhoneNumber(String)}. */
    public void setPhoneNumber(String phoneNumber) {
        prefs.edit().putString(KEY_PHONE_NUMBER, Validator.normalizePhoneNumber(phoneNumber)).apply();
    }
}
