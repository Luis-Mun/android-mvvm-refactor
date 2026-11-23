package com.example.firstapp_luismunoz.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Wraps the platform SMS API so framework code stays out of the ViewModel.
 *
 * <p>The original app called the deprecated {@code SmsManager.getDefault()} and showed "sent" as
 * soon as it handed the text over. Here the manager comes from {@code getSystemService}, and a
 * "sent" PendingIntent reports what the radio actually did. MainActivity listens for
 * {@link #ACTION_SMS_SENT} and shows the real result.
 */
public final class SmsNotifier {

    /** Broadcast action delivered, only to this app, when the SMS finishes sending or fails. */
    public static final String ACTION_SMS_SENT = "com.example.firstapp_luismunoz.SMS_SENT";

    private SmsNotifier() {}

    /**
     * Queues the message for sending.
     *
     * @return false if the device has no SMS service or rejected the request outright; true if it
     *     was queued, in which case the final result arrives as an {@link #ACTION_SMS_SENT} broadcast.
     */
    public static boolean send(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = context.getSystemService(SmsManager.class);
            if (smsManager == null) return false;
            Intent sentIntent = new Intent(ACTION_SMS_SENT).setPackage(context.getPackageName());
            PendingIntent sent = PendingIntent.getBroadcast(
                    context, 0, sentIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            smsManager.sendTextMessage(phoneNumber, null, message, sent, null);
            return true;
        } catch (RuntimeException e) {
            // SecurityException (permission revoked), IllegalArgumentException (bad number), etc.
            return false;
        }
    }
}
