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
import java.util.HashMap;

public class Languages extends AppCompatActivity {

    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        load_languages();
    }

    /**
     * Loads languages selected by user for displaying messages, messages in rest all
     * languages will be marked as spam
     * Hindi and English are marked by default
     */
    void load_languages(){
        ArrayList<String> languages = Language_Filter.languages;
        HashMap<String, Integer> languages_selected = new HashMap<>();

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.linear_layout_with_language_choice);

        mydatabase = openOrCreateDatabase("SpamJAM",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS languages(Language VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM languages;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String lang = resultSet.getString(0);
            languages_selected.put(lang, 1);
            resultSet.moveToNext();
        }

        for(int i = 0; i < languages.size(); i++){
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(languages.get(i));
            if(languages_selected.containsKey(languages.get(i))){
                checkBox.setChecked(true);
            }
            parentLayout.addView(checkBox);
        }
    }

    /**
     * Adds and removes languages in which the user needs messages
     */
    @Override
    protected void onDestroy() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_with_language_choice);
        for(int i = 0; i <  linearLayout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
            String lang = (String) checkBox.getText();
            if(checkBox.isChecked()) {
                mydatabase.execSQL("INSERT INTO languages VALUES (\"" + lang + "\");");
            } else {
                mydatabase.execSQL("DELETE FROM languages WHERE Language=\"" + lang + "\";");
            }
        }

        super.onDestroy();
    }
}
