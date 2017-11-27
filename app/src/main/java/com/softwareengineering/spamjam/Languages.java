package com.softwareengineering.spamjam;

import android.content.SharedPreferences;
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

        getSupportActionBar().setTitle("Select Languages");  // provide compatibility to all the versions
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6E00")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * Loads languages selected by user for displaying messages, messages in rest all
     * languages will be marked as spam
     * Hindi and English are marked by default
     */
    void load_languages(){
        ArrayList<String> languages = LanguageFilter.languages;
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
            checkBox.setTextSize(24);
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

        int number_of_changes = 0;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_with_language_choice);
        for(int i = 0; i <  linearLayout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
            String lang = (String) checkBox.getText();
            if(checkBox.isChecked()) {
                mydatabase.execSQL("INSERT INTO languages VALUES (\"" + lang + "\");");
                number_of_changes++;
            } else {
                mydatabase.execSQL("DELETE FROM languages WHERE Language=\"" + lang + "\";");
                number_of_changes++;
            }
        }

        if (number_of_changes != 0) {
            SharedPreferences.Editor editor = getSharedPreferences("SpamJAM", MODE_PRIVATE).edit();
            editor.putInt("classify", 12);
            editor.apply();
        }

        super.onDestroy();
    }
}
