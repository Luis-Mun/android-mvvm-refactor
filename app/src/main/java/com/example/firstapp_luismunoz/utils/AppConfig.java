package com.example.firstapp_luismunoz.utils;

/** App-wide constants that used to be hard-coded inside MainActivity. */
public final class AppConfig {

    /** Placeholder recipient for low-stock SMS alerts. Replace with a user setting. */
    public static final String ALERT_PHONE_NUMBER = "5551234567";

    /** An SMS alert fires when an item's quantity is at or below this value. */
    public static final int LOW_STOCK_THRESHOLD = 0;

    private AppConfig() {}
}
