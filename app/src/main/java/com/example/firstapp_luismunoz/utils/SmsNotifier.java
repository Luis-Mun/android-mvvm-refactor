package com.example.firstapp_luismunoz.utils;

import android.content.Context;
import android.telephony.SmsManager;

/** Thin wrapper over the platform SMS API (framework code stays out of the ViewModel). */
public final class SmsNotifier {

    private SmsNotifier() {}

    /** @return true if the message was handed to the telephony stack. */
    public static boolean send(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = context.getSystemService(SmsManager.class);
            if (smsManager == null) return false;
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
