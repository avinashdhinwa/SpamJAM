package com.softwareengineering.spamjam;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainMessages extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messages);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainMessages.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        readMessages();
    }

    void readMessages(){
        String INBOX = "content://sms/inbox";

        Cursor cursor = getContentResolver().query(Uri.parse(INBOX), null, null, null, null);

        ListView listView = (ListView)findViewById(R.id.all_messages);

        List<String> messages_list = new ArrayList<>();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                /*String msgData = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }*/

                //String msgData += cursor.getColumnName(13) + ":" + cursor.getString(13);
                String msgData = cursor.getString(13);

                messages_list.add(msgData);

                // use msgData
            } while (cursor.moveToNext());
        } else {
            Log.e("Error", "no messages");
        }

        ArrayAdapter<String> arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, messages_list.toArray());

        listView.setAdapter(arrayAdapter);

    }

}
