package com.softwareengineering.spamjam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        save_all_languages();
    }

    void save_all_languages(){

    }
}
