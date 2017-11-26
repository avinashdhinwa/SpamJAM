package com.softwareengineering.spamjam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Unknown User on 30-10-2017.
 */

public class Classifier {

    static HashMap<Integer, Integer> messages_classified = new HashMap<>();
    SQLiteDatabase mydatabase;
    NBC_Classifier nbc_classifier;
    HashSet<String> acceptedLanguages;
    HashSet<String> blackList;
    HashSet<String> whiteList;

    public Classifier(Context context){

        nbc_classifier = new NBC_Classifier(context);

        init_database(context);

        load_languages();
        load_blacklist();
        load_whitelist();

    }

    private void init_database(Context context) {
        mydatabase = context.openOrCreateDatabase("SpamJAM", Context.MODE_PRIVATE,null);

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS languages(Language VARCHAR);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS blacklisted(Address VARCHAR);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS whitelisted(Address VARCHAR);");
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

        return nbc_classifier.classify(message);
    }

    public HashMap<Integer, Integer> classify_all(HashMap<Integer, Message> id_to_messages){

        messages_classified.clear();

        Set<Integer> keys = id_to_messages.keySet();
        for (int key : keys) {
            if(id_to_messages.get(key).hard_coded == Message.YES){
                messages_classified.put(key, id_to_messages.get(key).spam);
            }
            else{
                String message = id_to_messages.get(key).body;
                String sender = id_to_messages.get(key).address;
                messages_classified.put(key, classify(message, sender));
            }
        }

        return messages_classified;
    }

    //    public HashMap<Integer, Integer> retrain_the_model(HashMap<Integer, Message> id_to_messages,
    public void retrain_the_model(HashMap<Integer, Message> id_to_messages,
                                                       HashMap<Integer, String> spam_messages_training,
                                                       HashMap<Integer, String> ham_messages_training) throws IOException {

        messages_classified.clear();

        /*nbc_classifier.fillTable(spam_messages_training, ham_messages_training);
        nbc_classifier.fillTableHindi(spam_messages_training, ham_messages_training);
*/
        HashMap<Integer, String> messages_dataSet = new HashMap<>();

        Set<Integer> keys = id_to_messages.keySet();
        for (int key : keys) {
            if(id_to_messages.get(key).hard_coded == Message.YES){
                messages_classified.put(key, id_to_messages.get(key).spam);
            }
            else{
                messages_dataSet.put(key, id_to_messages.get(key).body);
            }
        }

        nbc_classifier.fillTable(spam_messages_training, ham_messages_training);
        Log.e("abcd", "Classifier is called!!!");
//        messages_classified.putAll(nbc_classifier.classify_all(messages_dataSet));

//        return messages_classified;
    }

    private void load_blacklist() {
        blackList = new HashSet<>();

        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM blacklisted;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String address = resultSet.getString(0);
            Log.e("listing", "blacklists:" +  address);
            blackList.add(address);
            resultSet.moveToNext();
        }
    }

    private void load_whitelist() {
        whiteList = new HashSet<>();

        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM whitelisted;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String address = resultSet.getString(0);
            Log.e("listing", "whitelists:" +  address);
            whiteList.add(address);
            resultSet.moveToNext();
        }
    }

    private void load_languages() {
        acceptedLanguages = new HashSet<>();

        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM languages;", null);

        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){
            String lang = resultSet.getString(0);
            acceptedLanguages.add(lang);
            resultSet.moveToNext();
        }

        if(acceptedLanguages.size() == 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put("Language", Language_Filter.ENGLISH);
            contentValues.put("Language", Language_Filter.HINDI);
            mydatabase.insert("languages", null, contentValues);
            acceptedLanguages.add(Language_Filter.ENGLISH);
            acceptedLanguages.add(Language_Filter.HINDI);
        }
    }

    public void addSenderToListAndRemoveFromOther(String address, String addTo, String removeFrom) {

        int flag = 0;

        if(addTo.equals("blacklisted")) {
            if (blackList.contains(address)){
                return;
            }
            blackList.add(address);
            if(whiteList.contains(address)){
                whiteList.remove(address);
                flag = 1;
            }
        }
        else if(addTo.equals("whitelisted")) {
            if (whiteList.contains(address)){
                return;
            }
            whiteList.add(address);
            if(blackList.contains(address)){
                blackList.remove(address);
                flag = 1;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("Address", address);
        mydatabase.insert(addTo, null, contentValues);

        if(flag == 1) {
            Log.e("listing", "removing " + address + " from " + removeFrom);
            mydatabase.delete(removeFrom, "Address=?", new String[]{address});
        }
    }
}
