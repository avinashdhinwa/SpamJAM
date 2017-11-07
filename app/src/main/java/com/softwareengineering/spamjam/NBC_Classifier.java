package com.softwareengineering.spamjam;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Deepak on 04-11-2017.
 */

public class NBC_Classifier {

    static Hashtable<String,Double> spamWords = new Hashtable<>();
    static Hashtable<String,Double> hamWords = new Hashtable<>();
    static int spamCount = 0;
    static int hamCount = 0;

    public static void fillTable(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham)  throws IOException
    {
        String message;
        spamWords.clear();
        hamWords.clear();

        final String path = "res/raw/dataset.txt";
        File f = new File(path);
        InputStream inputStream = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while((line = br.readLine()) != null){
            line = line.toLowerCase();
            String [] spliter = line.split("\\t");

            if(spliter[0].equals("inbox"))
            {
                String result = MessageCleaning.messageCleaning(spliter[1]);

                String [] msgWords = result.split("\\s+");


                hamCount += msgWords.length;

                for(String s : msgWords)
                {
                    if(!hamWords.containsKey(s))
                    {
                        hamWords.put(s, 1.0);
                    }
                    else
                    {
                        hamWords.put(s, hamWords.get(s)+1);
                    }
                }

            }
            else
            {
                String result = MessageCleaning.messageCleaning(spliter[1]);
                String [] msgWords = result.split("\\s");
                spamCount += msgWords.length;

                for(String s : msgWords)
                {
                    if(!spamWords.containsKey(s))
                    {
                        spamWords.put(s, 1.0);
                    }
                    else
                    {
                        spamWords.put(s, spamWords.get(s)+1);
                    }
                }

            }
        }

        Set<Integer> keys = Ham.keySet();
        for (int key : keys){
            message = Ham.get(key).toLowerCase();
            String lang = Language_Filter.predictor(message);

            if(lang.equals("English")) {
                message = MessageCleaning.messageCleaning(message);
                String[] msgWords = message.split("\\s+");

                hamCount += msgWords.length;

                for (String s : msgWords) {
                    if (!hamWords.containsKey(s)) {
                        hamWords.put(s, 1.0);
                    } else {
                        hamWords.put(s, hamWords.get(s) + 1);
                    }
                }
            }
        }

        keys = Spam.keySet();
        for (int key : keys){
            message = Spam.get(key).toLowerCase();
            String lang = Language_Filter.predictor(message);

            if(lang.equals("English")) {
                message = MessageCleaning.messageCleaning(message);
                String[] msgWords = message.split("\\s+");

                spamCount += msgWords.length;

                for (String s : msgWords) {
                    if (!spamWords.containsKey(s)) {
                        spamWords.put(s, 1.0);
                    } else {
                        spamWords.put(s, spamWords.get(s) + 1);
                    }
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
        String lang = Language_Filter.predictor(message);
        if(lang.equals("English")) {
            message = MessageCleaning.messageCleaning(message);
            String[] msgWords = message.split("\\s+");
            double hamProb = hamCount * 1.0 / (hamCount + spamCount);
            double spamProb = spamCount * 1.0 / (spamCount + hamCount);

            for (String s : msgWords) {
                if (spamWords.containsKey(s)) {
                    spamProb *= spamWords.get(s);
                } else {
                    spamProb *= (1.0 / spamCount);
                }


                if (hamWords.containsKey(s)) {
                    hamProb *= hamWords.get(s);
                } else {
                    hamProb *= (1.0 / hamCount);
                }

            }

            if (hamProb >= spamProb)
                return Message.NOT_SPAM;
            else
                return Message.SPAM;
        }
        else{
            return Message.SPAM;
        }

    }

    public static HashMap<Integer, Integer> classify(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham, HashMap<Integer, String> dataSet) throws IOException{

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
            //Log.e("Red", key + " : " + dataSet.get(key));
            String message = dataSet.get(key).toLowerCase();
            spam_or_ham.put(key, classifier(message));
        }

        return spam_or_ham;
    }

}