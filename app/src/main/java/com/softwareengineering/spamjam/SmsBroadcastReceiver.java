package com.softwareengineering.spamjam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    static String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public SmsBroadcastReceiver() {
    }

    /**
     * Sends new message to the 'ReceivedMessages' activity
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        Log.d("broadcast", "intent action : " + intent.getAction());

        if(intent.getAction().equals(SMS_RECEIVED)) {
            if (bundle != null) {

                Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String address = currentMessage.getDisplayOriginatingAddress();
                    String person = address;
                    String body = currentMessage.getDisplayMessageBody();
                    int id = ReceivedMessages.max_id + 1;
                    ReceivedMessages.max_id++;
                    String date = Message.millisToTime(currentMessage.getTimestampMillis());

                    Message message = new Message(id, body, person, address, date);
                    message.message_type = Message.INBOX;
                    Log.d("broadcast", "Recieved Message : " + message.to_string_for_debug());

                    EventBus.getDefault().post(message);
                }
            }
        }
    }
}
