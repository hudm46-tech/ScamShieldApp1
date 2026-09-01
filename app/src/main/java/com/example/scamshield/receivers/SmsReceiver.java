package com.example.scamshield.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.example.scamshield.database.DatabaseHelper;
import com.example.scamshield.utils.NotificationHelper;
import com.example.scamshield.utils.ScamDetector;
import com.example.scamshield.utils.WhitelistUtils;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;

        Log.d(TAG, "SMS Received: " + intent.getAction());

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String sender = null;
            String message = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for (SmsMessage smsMessage : messages) {
                    sender = smsMessage.getDisplayOriginatingAddress();
                    message = smsMessage.getMessageBody();
                    break;
                }
            } else {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                            sender = smsMessage.getDisplayOriginatingAddress();
                            message = smsMessage.getMessageBody();
                            break;
                        }
                    }
                }
            }

            if (sender != null && message != null) {
                Log.d(TAG, "From: " + sender + " | Message: " + message);

                // ✅ CHECK IF SERVICE MESSAGE - DON'T ALERT
                if (WhitelistUtils.isServiceMessage(sender, message)) {
                    Log.d(TAG, "Service message - IGNORED");
                    return;
                }

                checkForScam(context, sender, message);
            }
        }
    }

    private void checkForScam(Context context, String sender, String message) {
        // Use ScamDetector
        ScamDetector.ScamResult result = ScamDetector.detect(message, sender);

        if (result.isScam) {
            // Save to database
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            long id = dbHelper.addScam(sender, message, result.scamType);
            Log.d(TAG, "Scam saved. ID: " + id + " | Type: " + result.scamType);

            // Show notification
            NotificationHelper.createNotificationChannel(context);
            NotificationHelper.showScamAlert(context, sender, result.scamType + " (" + result.dangerLevel + ")");
        }
    }
}