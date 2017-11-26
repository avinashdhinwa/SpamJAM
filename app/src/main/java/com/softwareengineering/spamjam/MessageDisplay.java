package com.softwareengineering.spamjam;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by goldie on 11/25/2017.
 */

public class MessageDisplay extends AppCompatActivity {

    Button send;
    EditText ed;
    String s[];
    RelativeLayout r;

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
        send = (Button) findViewById(R.id.button2);
        ed = (EditText) findViewById(R.id.editText4);

        r = (RelativeLayout) findViewById(R.id.unknown);

        s = message.split("`");

        /**
         * Changing the color of Action bar
         */
        getSupportActionBar().setTitle(s[2]);  // provide compatibility to all the versions
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6E00")));


        String cal1[] = s[1].split(" ");
        cal1[1].trim();
        String cal[] = cal1[1].split("-");


        /**
         * Retrieving the weekDay from the Date
         */
        Calendar calendar = new GregorianCalendar();


        calendar.set(Integer.parseInt(cal[2]), Integer.parseInt(cal[1]), Integer.parseInt(cal[0]));

        //Log.d("Tag" , cal[2]+" "+cal[1]+" "+cal[0]);

        String[] days = new String[]{"", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};

        String day = days[calendar.get(Calendar.DAY_OF_WEEK)];

        String[] month = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};


        text.setText(s[0]);
        time.setText(day + ", " + month[Integer.parseInt(cal[1])] + " " + cal[0] + " " + cal1[0]);


        /**
         * Checking for authorised sender and disablng sending view for spam user
         */
        if (!(s[2].charAt(1) >= '0' && s[2].charAt(1) <= '9' && s[2].charAt(2) >= '0' && s[2].charAt(2) <= '9')) {
            r.setVisibility(View.GONE);
        }


        /**
         * on click listener for sending message button
         */
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneNo = s[2];
                String message = ed.getText().toString();
                if (phoneNo.length() > 9 && message.length() > 0 && phoneNo.charAt(1) >= '0' && phoneNo.charAt(1) <= '9' && phoneNo.charAt(2) >= '0' && phoneNo.charAt(2) <= '9') {
                    sendSMS(phoneNo, message);
                } else
                    ed.setText("Sender do not take reply");

            }
        });


    }


    /**
     * @param phoneNo = stores the phoneNo of message ouwner
     * @param message = actual message body
     */
    protected void sendSMS(String phoneNo, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();

            ed.setText("");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            ed.setText("SMS faild, please try again." + " ✘");
        }
    }


}
