package com.example.mapx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = SMSReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        String messageStr = "";

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                messageStr += messages[i].getMessageBody();
            }

            Log.d(TAG, "Received SMS: " + messageStr);

            // Extract latitude and longitude values from message
            String[] parts = messageStr.split(",");
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());

            // Pass latitude and longitude values to map activity
            Intent mapIntent = new Intent(context, MapsActivity.class);
            mapIntent.putExtra("latitude", latitude);
            mapIntent.putExtra("longitude", longitude);
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mapIntent);
        }
    }
}
