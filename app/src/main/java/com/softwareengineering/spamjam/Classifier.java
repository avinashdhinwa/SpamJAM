package com.softwareengineering.spamjam;

import android.util.Log;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Unknown User on 30-10-2017.
 */

public class Classifier {

    static Hashtable<String,Double> spamWords = new Hashtable<>();
    static Hashtable<String,Double> hamWords = new Hashtable<>();
    static int spamCount = 0;
    static int hamCount = 0;

    public static void fillTable(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham)
    {
        String message;

        Set<Integer> keys = Ham.keySet();
        for (int key : keys){
            message = Ham.get(key).toLowerCase();
            String [] msgWords = message.split("\\s+");

            hamCount += msgWords.length;

            for(String s : msgWords){
                if(!hamWords.containsKey(s)){
                    hamWords.put(s, 1.0);
                }
                else{
                    hamWords.put(s, hamWords.get(s)+1);
                }
            }
        }

        keys = Spam.keySet();
        for (int key : keys){
            message = Spam.get(key).toLowerCase();
            String [] msgWords = message.split("\\s+");

            spamCount += msgWords.length;

            for(String s : msgWords){
                if(!spamWords.containsKey(s)){
                    spamWords.put(s, 1.0);
                }
                else{
                    spamWords.put(s, spamWords.get(s)+1);
                }
            }
        }

        Set<String> keySet = hamWords.keySet();
        for(String s: keySet)
        {
            hamWords.put(s, hamWords.get(s)/hamCount);
        }

        keySet = spamWords.keySet();
        for(String s: keySet)
        {
            spamWords.put(s, spamWords.get(s)/spamCount);
        }
    }

    public static int classifier(String message)
    {
        String [] msgWords = message.split("\\s+");
        double hamProb = hamCount*1.0/(hamCount+spamCount);
        double spamProb = spamCount*1.0/(spamCount+hamCount);

        for(String s : msgWords)
        {
            if(spamWords.containsKey(s))
            {
                spamProb *= spamWords.get(s);
            }
            else
            {
                spamProb *= (1.0/spamCount);
            }


            if(hamWords.containsKey(s))
            {
                hamProb *= hamWords.get(s);
            }
            else
            {
                hamProb *= (1.0/hamCount);
            }

        }

        if(hamProb >= spamProb)
            return Message.NOT_SPAM;
        else
            return Message.SPAM;

    }

    public static HashMap<Integer, Integer> classify(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham, HashMap<Integer, String> dataSet){

        fillTable(Spam, Ham);

        Set<String> keys_ = spamWords.keySet();
        for (String key : keys_) {
            Log.d("Probab Spam", key + " : " + spamWords.get(key));
        }
        keys_ = hamWords.keySet();
        for (String key : keys_) {
            Log.d("Probab Ham", key + " : " + hamWords.get(key));
        }

        HashMap<Integer, Integer> spam_or_ham = new HashMap<>();

        Set<Integer> keys = dataSet.keySet();
        for (int key : keys) {
            String message = dataSet.get(key).toLowerCase();
            spam_or_ham.put(key, classifier(message));
            if(spam_or_ham.get(key) == Message.SPAM) {
                Log.e("Red", key + " : " + dataSet.get(key));
            }
        }

        return spam_or_ham;
    }

}
