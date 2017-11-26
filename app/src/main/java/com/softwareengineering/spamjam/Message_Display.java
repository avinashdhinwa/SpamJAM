package com.softwareengineering.spamjam;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by goldie on 11/25/2017.
 */

public class Message_Display extends AppCompatActivity {


    @SuppressLint("ResourceType")
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display);

        String message = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            message = (String) extras.get("key");
            //The key argument here must match that used in the other activity
        }

        TextView text = (TextView) findViewById(R.id.textview);
        TextView time = (TextView) findViewById(R.id.time);

        // text.setBackgroundResource(text.getResources().getColor(android.R.color.holo_green_light));
        String s[] = message.split("`");
       // getActionBar().setTitle("Hello");
        getSupportActionBar().setTitle(s[2]);  // provide compatibility to all the versions
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6E00")));

        String cal1[] = s[1].split(" ");
        cal1[1].trim();
        String cal[] = cal1[1].split("-");


        Calendar calendar = new GregorianCalendar();


        calendar.set(Integer.parseInt(cal[2]), Integer.parseInt(cal[1]), Integer.parseInt(cal[0]));

        //Log.d("Tag" , cal[2]+" "+cal[1]+" "+cal[0]);

        String[] days = new String[] { "" , "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };

        String day = days[calendar.get(Calendar.DAY_OF_WEEK)];

        String[] month = new String[] { "Jan", "Feb", "March", "April", "May", "June", "July" , "Aug" , "Sept" , "Oct" , "Nov" , "Dec"};


        text.setText(s[0]);
        time.setText(day + ", "+month[Integer.parseInt(cal[1])] +" "+cal[0] +" " +cal1[0]);


    }
}
