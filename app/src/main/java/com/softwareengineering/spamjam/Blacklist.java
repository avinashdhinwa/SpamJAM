package com.softwareengineering.spamjam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class Blacklist extends AppCompatActivity {

    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        load_blacklist();

        getSupportActionBar().setTitle("BlackListed Contact");  // provide compatibility to all the versions
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6E00")));
    }

    void load_blacklist(){
        List<String> blackList = new ArrayList<>();

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.linear_layout_with_blacklist_contacts_choice);

        mydatabase = openOrCreateDatabase("SpamJAM",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS blacklisted(Address VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM blacklisted;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String address = resultSet.getString(0);
            blackList.add(address);
            resultSet.moveToNext();
        }

        for(int i = 0; i < blackList.size(); i++){
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(blackList.get(i));
            checkBox.setChecked(true);
            checkBox.setTextSize(24);
            parentLayout.addView(checkBox);
        }
    }

    @Override
    protected void onDestroy() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_with_blacklist_contacts_choice);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
            String address = (String) checkBox.getText();
            if (checkBox.isChecked()) {
            } else {
                mydatabase.delete("blacklisted", "Address=?", new String[]{address});
            }
        }

        super.onDestroy();
    }
}
