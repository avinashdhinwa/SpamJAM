package com.softwareengineering.spamjam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    static String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public SmsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        try {
//            this.wait(1000);
//        } catch (InterruptedException e) {
//            Log.e("Error", "can't wait");
//            e.printStackTrace();
//        }

        Bundle intentExtras = intent.getExtras();

        Log.e("broadcast", "intent action : " + intent.getAction());

        if(intent.getAction().equals(SMS_RECEIVED)) {
            if (intentExtras != null) {

                Object[] pdus = (Object[])intentExtras.get("pdus");
                String INBOX = "content://sms/inbox";
                Cursor cursor = context.getContentResolver().query(Uri.parse(INBOX), null, null, null, null);

                if (cursor.moveToFirst()) { // must check the result to prevent exception

                    int BODY = cursor.getColumnIndex("body");
                    int ID = cursor.getColumnIndex("_id");
                    int PERSON = cursor.getColumnIndex("person");
                    int ADDRESS = cursor.getColumnIndex("address");
                    int DATE = cursor.getColumnIndex("date");

                    for (int i = 0; !cursor.isAfterLast() && i < pdus.length; i++, cursor.moveToNext()) {

                        Log.e("broadcast", (i+1) + " message");

                        String body = cursor.getString(BODY);
                        int id = cursor.getInt(ID);
                        String person = cursor.getString(PERSON);
                        String date = Message.millisToTime(cursor.getLong(DATE));
                        String address = cursor.getString(ADDRESS);

                        Message message = new Message(id, body, person, address, date);
                        Log.e("broadcast", "Recieved Message : " + message.to_string_for_debug());

                        EventBus.getDefault().post(message);
                    }
                } else {
                    Log.e("broadcast", "no messages");
                }
            }
        }
    }
}
