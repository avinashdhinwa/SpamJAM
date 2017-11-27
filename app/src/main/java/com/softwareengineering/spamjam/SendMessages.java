package com.softwareengineering.spamjam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by goldie on 11/26/2017.
 */

public class SendMessages extends AppCompatActivity {

    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    TextView txtview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtview = (TextView) findViewById(R.id.textview);


        /**
         * Sending message using inbuilt smsmanager and listening on the send button to send messages
         */
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();
                if (phoneNo.length() > 9 && message.length() > 0) {
                    sendSMS(phoneNo, message);
                } else
                    Toast.makeText(getBaseContext(),
                            "Please enter both phone number and message.",
                            Toast.LENGTH_SHORT).show();
            }
        });


    }


    /**
     * @param phoneNo phoneNo of message owber
     * @param message actual message body
     *                Sending message via smsManager
     */
    protected void sendSMS(String phoneNo, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();

            txtview.setText(message + " ✓✓");
            txtMessage.setText("");
            txtPhoneNo.setText("");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            txtview.setText("SMS faild, please try again." + " ✘");
        }
    }

}
