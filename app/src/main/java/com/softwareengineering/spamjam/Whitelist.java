package com.softwareengineering.spamjam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class Whitelist extends AppCompatActivity {

    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        load_whitelist();
    }

    /**
     * Loads the contacts which are marked as whitelisted
     */
    void load_whitelist(){
        List<String> whiteList = new ArrayList<>();

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.linear_layout_with_whitelist_contacts_choice);

        mydatabase = openOrCreateDatabase("SpamJAM",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS whitelisted(Address VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM whitelisted;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String address = resultSet.getString(0);
            whiteList.add(address);
            resultSet.moveToNext();
        }

        for(int i = 0; i < whiteList.size(); i++){
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(whiteList.get(i));
            checkBox.setChecked(true);
            parentLayout.addView(checkBox);
        }
    }

    /**
     * Deletes entries in whitelist which are unmarked
     */
    @Override
    protected void onDestroy() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_with_whitelist_contacts_choice);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
            String address = (String) checkBox.getText();
            if (checkBox.isChecked()) {
            } else {
                mydatabase.delete("whitelisted", "Address=?", new String[]{address});
            }
        }

        super.onDestroy();
    }
}
