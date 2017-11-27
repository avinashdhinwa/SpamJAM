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
    NBC_Classifier nbc_classifier;
    HashSet<String> acceptedLanguages;
    HashSet<String> blackList;
    HashSet<String> whiteList;
    private SQLiteDatabase mydatabase;

    public Classifier(Context context){

        nbc_classifier = new NBC_Classifier(context);

        init_database(context);

        load_languages();
        load_blacklist();
        load_whitelist();

    }

    /**
     * Opens connection with database
     *
     * @param context needed for opening database
     */
    private void init_database(Context context) {
        mydatabase = context.openOrCreateDatabase("SpamJAM", Context.MODE_PRIVATE,null);

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS languages(Language VARCHAR);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS blacklisted(Address VARCHAR);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS whitelisted(Address VARCHAR);");
    }

    /**Classifies a given message into spam or non-spam
     *
     * @param message body of a message (needed for classifying by machine learning model or language)
     * @param sender sender of the message (needed for checking if present in blacklist or whitelist)
     * @return spam or non-spam
     */
    public int classify(String message, String sender){

        String lang = LanguageFilter.predictor(message);

        if(blackList.contains(sender)){
            return Message.SPAM;
        } else if(whiteList.contains(sender)){
            return Message.NOT_SPAM;
        } else if(!acceptedLanguages.contains(lang)){
            return Message.SPAM;
        }

        return nbc_classifier.classify(message);
    }

    /** Classifies all messages passed as hashmap of message id and their Message object
     *
     * @param id_to_messages Hashmap of message id's and their corresponding Object of Message
     * @return classified messages
     */
    public HashMap<Integer, Integer> classify_all(HashMap<Integer, Message> id_to_messages){

        messages_classified.clear();

        Set<Integer> keys = id_to_messages.keySet();
        for (int key : keys) {
            // don't classify messages which are part of training set
            if(id_to_messages.get(key).hard_coded == Message.YES){
                messages_classified.put(key, id_to_messages.get(key).spam);
            } else{
                String message = id_to_messages.get(key).body;
                String sender = id_to_messages.get(key).address;
                messages_classified.put(key, classify(message, sender));
                Log.d("changes", "classifying : " + message);
            }
        }

        return messages_classified;
    }

    /**
     * Retrains the classifying model
     *
     * @param spam_messages_training Hashmap of message (spam messages) id's and message body
     * @param ham_messages_training  Hashmap of message (non-spam messages) id's and message body
     * @throws IOException
     */
    public void retrain_the_model(HashMap<Integer, String> spam_messages_training,
                                  HashMap<Integer, String> ham_messages_training) throws IOException {

        nbc_classifier.fillTable(spam_messages_training, ham_messages_training);
    }

    /**
     *  Load contacts which must never be marked non-spam or inbox
     */
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

    /**
     *  Loads contacts which must never be marked spam
     */
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

    /**
     *  Loads languages which are acceptable to the user
     */
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
            contentValues.put("Language", LanguageFilter.ENGLISH);
            mydatabase.insert("languages", null, contentValues);
            contentValues.put("Language", LanguageFilter.HINDI);
            mydatabase.insert("languages", null, contentValues);
            acceptedLanguages.add(LanguageFilter.ENGLISH);
            acceptedLanguages.add(LanguageFilter.HINDI);
        }
    }

    /** Adds and removes contacts from/to blacklist and whitelist
     *
     * @param address Sender whom we need to blacklist/whitelist
     * @param addTo Table (either blacklist/whitelist) to which we need to add this contact/sender
     * @param removeFrom Table (either blacklist/whitelist) from which we need to remove this contact/sender
     */
    public void addSenderToListAndRemoveFromOther(String address, String addTo, String removeFrom) {

        int flag = 0;


        if(addTo.equals("blacklisted")) {
            if (blackList.contains(address)){
                return;
            }
            blackList.add(address);
            // checking if contact needs to be removed or not
            if(whiteList.contains(address)){
                whiteList.remove(address);
                flag = 1;
            }
        } else if(addTo.equals("whitelisted")) {
            if (whiteList.contains(address)){
                return;
            }
            whiteList.add(address);
            // checking if contact needs to be removed or not
            if(blackList.contains(address)){
                blackList.remove(address);
                flag = 1;
            }
        }

        // adding oontact to the table
        ContentValues contentValues = new ContentValues();
        contentValues.put("Address", address);
        mydatabase.insert(addTo, null, contentValues);

        // remove contact from the table
        if(flag == 1) {
            mydatabase.delete(removeFrom, "Address=?", new String[]{address});
        }
    }
}
