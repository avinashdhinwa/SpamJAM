package com.softwareengineering.spamjam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by Unknown User on 30-10-2017.
 */

public class Classifier {

    List<String> acceptedLanguages;
    List<String> blackList;
    List<String> whiteList;

    public Classifier(){

        load_languages();
        load_blacklist();
        load_whitelist();

    }

    public int classify(String message, String sender){

        String lang = Language_Filter.predictor(message);

        if(blackList.contains(sender)){
            return Message.SPAM;
        }
        else if(whiteList.contains(sender)){
            return Message.NOT_SPAM;
        }
        else if(!acceptedLanguages.contains(lang)){
            return Message.SPAM;
        }

        return Message.NOT_SPAM;
    }

    public void classify_all(){

        String message = "hi";
        String sender = "aashirwad";
        classify(message, sender);
    }

    private void load_blacklist() {
        blackList = new ArrayList<>();

        SQLiteDatabase mydatabase = openOrCreateDatabase("SpamJAM", null,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS blacklisted(Address VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM blacklisted;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String address = resultSet.getString(0);
            blackList.add(address);
            resultSet.moveToNext();
        }
    }

    private void load_whitelist() {
        whiteList = new ArrayList<>();

        SQLiteDatabase mydatabase = openOrCreateDatabase("SpamJAM", null,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS whitelisted(Address VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM whitelisted;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String address = resultSet.getString(0);
            whiteList.add(address);
            resultSet.moveToNext();
        }
    }

    private void load_languages() {
        acceptedLanguages = new ArrayList<>();

        SQLiteDatabase mydatabase = openOrCreateDatabase("SpamJAM", null,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS languages(Language VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM languages;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String lang = resultSet.getString(0);
            acceptedLanguages.add(lang);
            resultSet.moveToNext();
        }

        if(acceptedLanguages.size() == 0){
            mydatabase.execSQL("INSERT INTO languages VALUES (\"" + Language_Filter.ENGLISH + "\");");
            acceptedLanguages.add(Language_Filter.ENGLISH);
        }
    }

}
