package com.softwareengineering.spamjam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class Settings extends AppCompatActivity {

    SQLiteDatabase mydatabase = openOrCreateDatabase("SpamJAM",MODE_PRIVATE,null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        load_languages();
    }

    void load_languages(){
        ArrayList<String> languages = Language_Filter.languages;
        HashMap<String, Integer> languages_selected = new HashMap<>();

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.linear_layout_with_language_choice);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
