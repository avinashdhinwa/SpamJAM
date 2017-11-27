package com.softwareengineering.spamjam;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Deepak on 04-11-2017.
 */

public class NBC_Classifier{

    Context context;
    int size = 2;
    Hashtable<String, Double> spamWordsArray[] = new Hashtable[size];
    Hashtable<String, Double> hamWordsArray[] = new Hashtable[size];
    int spamCountArray[] = new int[size];
    int hamCountArray[] = new int[size];
    int english = 0;
    int hindi = 1;

    /**
     * constructor of NBC classifier
     *
     * @param ctx context of main activity
     */
    public NBC_Classifier(Context ctx) {
        context = ctx;
        // initialize all hash table
        for (int i = 0; i < size; i++) {
            spamCountArray[i] = 0;
            hamCountArray[i] = 0;
            spamWordsArray[i] = new Hashtable<String, Double>();
            hamWordsArray[i] = new Hashtable<String, Double>();
        }
        // load classifier
        load_classifier();

    }

    /**
     * method which will load learned model if exists otherwise retrain the NBC model
     */
    private void load_classifier() {
        try {
            File english_model = new File(context.getFilesDir(), "english_model"); // saved english model
            File hindi_model = new File(context.getFilesDir(), "hindi_model"); // saved hindi model
            if (english_model.exists() && hindi_model.exists()) { // check if exists
                Scanner scanner = new Scanner(new FileReader(english_model));
                // load classifier
                while (scanner.hasNextLine()) {
                    String word = scanner.next();
                    Double spamValue = scanner.nextDouble();
                    Double hamValue = scanner.nextDouble();
                    // add word in spam list if spam probability is non-zero
                    if (spamValue != 0) {
                        spamWordsArray[english].put(word, spamValue);
                    }
                    // add word in ham list if ham probability is non-zero
                    if (hamValue != 0) {
                        hamWordsArray[english].put(word, hamValue);
                    }
                    //Log.d("savedloading",word+" "+spamValue+" "+hamValue);

                }
                scanner.close();

                scanner = new Scanner(new FileReader(hindi_model));

                // same do with hindi words
                while (scanner.hasNextLine()) {
                    String word = scanner.next();
                    Double spamValue = scanner.nextDouble();
                    Double hamValue = scanner.nextDouble();
                    // add word in spam list if spam probability is non-zero
                    if (spamValue != 0) {
                        spamWordsArray[hindi].put(word, spamValue);
                    }
                    // add word in ham list if ham probability is non-zero
                    if (hamValue != 0) {
                        hamWordsArray[hindi].put(word, hamValue);
                    }
                }
            } else {
                // train model
                fillTable(new HashMap<Integer, String>(), new HashMap<Integer, String>());
            }
            Log.d("debug", "loading classifer");
            Log.d("debug", "Spam count english : " + spamCountArray[english]);
            Log.d("debug", "ham count english : " + hamCountArray[english]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method is to read the data set from file
     *
     * @param ctx context of main activity
     */
    public void readDataSetFromFileInTable(Context ctx) {
        // id for english model
        int EnglishId = R.raw.dataset_english;
        // input stream for english
        InputStream inputStream = ctx.getResources().openRawResource(EnglishId);

        Scanner scanner = new Scanner(inputStream);
        // read data set from file
        while (scanner.hasNextLine()) {
            String word = scanner.next(); // message word
            int spamValue = scanner.nextInt(); // spam count for corresponding word
            int hamValue = scanner.nextInt(); // ham count for same word
            // add
            if (spamValue != 0) {
                spamWordsArray[english].put(word, spamValue * 1.0);
                spamCountArray[english] += spamValue;
            }
            if (hamValue != 0) {
                hamWordsArray[english].put(word, hamValue * 1.0);
                hamCountArray[english] += hamValue;
            }
        }

        // resource  id for hindi
        int HindiId = R.raw.dataset_hindi;
        inputStream = ctx.getResources().openRawResource(HindiId);
        scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            String word = scanner.next();
            int spamValue = scanner.nextInt();
            int hamValue = scanner.nextInt();
            // add word in spam list if spam probability is non-zero
            if (spamValue != 0) {
                spamWordsArray[hindi].put(word, spamValue * 1.0);
                spamCountArray[hindi] += spamValue;
            }
            // add word in ham list if ham probability is non-zero
            if (hamValue != 0) {
                hamWordsArray[hindi].put(word, hamValue * 1.0);
                hamCountArray[hindi] += hamValue;
            }
        }

        scanner.close(); // close the scanner
    }


    /**
     * this method it to train the NBC model
     *
     * @param Spam marked spam set by user
     * @param Ham  marked ham set by user
     * @throws IOException throws exception if file is not found
     */
    public void fillTable(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham) throws IOException {

        String message; // message string
        // clear previous list
        for (int i = 0; i < size; i++) {
            spamWordsArray[i].clear();
            hamWordsArray[i].clear();
            spamCountArray[i] = 0;
            hamCountArray[i] = 0;
        }

        // read dataset form resource files
        readDataSetFromFileInTable(context);


        Set<Integer> keys = Ham.keySet(); // hamkey set
        for (int key : keys){
            message = Ham.get(key).toLowerCase();
            String lang = LanguageFilter.predictor(message);
            // add in english table if language is english
            if(lang.equals("English")) {
                message = MessageCleaning.newWordCleaning(message);//MessageCleaning.messageCleaning(message);
                String[] msgWords = message.split("\\s+");//message.split("[\\\\s|\\\\t|.|/|,|:|!|'|=|+|\n|-]+");

                hamCountArray[english] += msgWords.length;

                for (String s : msgWords) {
                    if (!hamWordsArray[english].containsKey(s)) {
                        hamWordsArray[english].put(s, 1.0);
                    } else {
                        hamWordsArray[english].put(s, hamWordsArray[english].get(s) + 1.0);
                    }
                }
            }

            // add in hindi table if language is hindi
            else if (lang.equals("Hindi")) {
                String[] msgWords = MessageCleaning.HindiMessageCleaning(message.toLowerCase()).split("\\s+");//message.split("[\\s|;|:|,|)|(|{|}|[|]|/| |-|\n]+");

                hamCountArray[hindi] += msgWords.length;
                // go through all words
                for (String s : msgWords) {
                    if (!hamWordsArray[hindi].containsKey(s)) {
                        hamWordsArray[hindi].put(s, 1.0);
                    } else {
                        hamWordsArray[hindi].put(s, hamWordsArray[hindi].get(s) + 1.0);
                    }
                }
            }
        }

        // same do with user marked spam list
        keys = Spam.keySet();
        for (int key : keys){
            message = Spam.get(key).toLowerCase();
            String lang = LanguageFilter.predictor(message);

            if(lang.equals("English")) {
                message = MessageCleaning.newWordCleaning(message);//MessageCleaning.messageCleaning(message);
                String[] msgWords = message.split("\\s+");//message.split("[\\\\s|\\\\t|.|/|,|:|!|'|=|+|\n|-]+");

                spamCountArray[english] += msgWords.length;

                for (String s : msgWords) {
                    if (!spamWordsArray[english].containsKey(s)) {
                        spamWordsArray[english].put(s, 1.0);
                    } else {
                        spamWordsArray[english].put(s, spamWordsArray[english].get(s) + 1);
                    }
                }
            } else if (lang.equals("Hindi")) {
                String[] msgWords = MessageCleaning.HindiMessageCleaning(message.toLowerCase()).split("\\s+");//message.split("[\\s|;|:|,|)|(|{|}|[|]|/| |-|\n]+");

                spamCountArray[hindi] += msgWords.length;

                for (String s : msgWords) {
                    if (!spamWordsArray[hindi].containsKey(s)) {
                        spamWordsArray[hindi].put(s, 1.0);
                    } else {
                        spamWordsArray[hindi].put(s, spamWordsArray[hindi].get(s) + 1.0);
                    }
                }
            }
        }

        for (int i = 0; i < size; i++) {
            Set<String> keySet = hamWordsArray[i].keySet();
            for (String s : keySet) {
                hamWordsArray[i].put(s, hamWordsArray[i].get(s) / hamCountArray[i]);
            }

            keySet = spamWordsArray[i].keySet();
            for (String s : keySet) {
                spamWordsArray[i].put(s, spamWordsArray[i].get(s) / spamCountArray[i]);
            }
        }


        Log.e("printer", "spam count = " + spamCountArray[english]);
        Log.e("printer", "ham Count = " + hamCountArray[english]);

    }

    /**
     * this method will save the learned model of NBC classifier in txt file
     *
     * @throws IOException it will throws exception if file is not created
     */
    public void saveClassifier() throws IOException {
        File f = new File(context.getFilesDir(), "english_model"); // create a new file
        FileWriter fw = new FileWriter(f);
        Set<String> keys = spamWordsArray[english].keySet();
        Double value;
        // write in file all words
        StringBuilder eng = new StringBuilder();
        for (String key : keys) {
            if (hamWordsArray[english].containsKey(key)) {
                value = hamWordsArray[english].get(key);
                hamWordsArray[english].remove(key);
            } else {
                value = 0.0;
            }
            //Log.d("saved",key+" "+value+" "+spamWordsArray[english].get(key));
            eng.append(key + "\t" + spamWordsArray[english].get(key) + "\t" + value + "\n");
        }

        // write in file all words of ham table
        keys = hamWordsArray[english].keySet();
        for (String key : keys) {
            eng.append(key + "\t" + "0.0" + "\t" + hamWordsArray[english].get(key) + "\n");
        }
        fw.write(eng.toString().substring(0, eng.length() - 1));
        fw.close();

        // save the hindi model also in file
        f = new File(context.getFilesDir(), "hindi_model");
        fw = new FileWriter(f);
        keys = spamWordsArray[hindi].keySet();
        StringBuilder hin = new StringBuilder();
        for (String key : keys) {
            if (hamWordsArray[hindi].containsKey(key)) {
                value = hamWordsArray[hindi].get(key);
                hamWordsArray[hindi].remove(key);
            } else {
                value = 0.0;
            }
            hin.append(key + "\t" + spamWordsArray[hindi].get(key) + "\t" + value + "\n");
        }

        keys = hamWordsArray[hindi].keySet();
        for (String key : keys) {
            hin.append(key + "\t" + "0.0" + "\t" + hamWordsArray[hindi].get(key) + "\n");
        }
        fw.write(hin.toString().substring(0, hin.length() - 1));
        fw.close();

        // clear all table after saving the both models
        for (int i = 0; i < size; i++) {
            spamWordsArray[i].clear();
            hamWordsArray[i].clear();
        }


    }

    /**
     * it will classifiy  given message in spam/ham
     *
     * @param message given message which we want to classify
     * @return return 1 of message is ham other wise 0
     */
    public int classify(String message) {
        message = message.toLowerCase(); // change message in lower case
        String lang = LanguageFilter.predictor(message); // predict the language of message

        // classifiy using english model if language is english
        if (lang.equals("English")) {
            // clean the message
            message = MessageCleaning.newWordCleaning(message);//MessageCleaning.messageCleaning(message);
            String[] msgWords = message.split("\\s+"); // split bases on white spaces
            double hamProb = hamCountArray[english] * 1.0 / (hamCountArray[english] + spamCountArray[english]); // ham probability
            double spamProb = spamCountArray[english] * 1.0 / (spamCountArray[english] + hamCountArray[english]); // spam probability
            double spValue, hmValue;
            // go through all words of message
            for (String s : msgWords) {
                // get probability if exist in table
                if (spamWordsArray[english].containsKey(s)) {
                    spValue = spamWordsArray[english].get(s);
                } else {
                    // take constant probability if not exist
                    spValue = (1.0 / (spamCountArray[english]+hamCountArray[english]));
                }

                // get probability if exist in table
                if (hamWordsArray[english].containsKey(s)) {
                    hmValue = hamWordsArray[english].get(s);
                } else {
                    // take constant probability if not exist
                    hmValue = (1.0 / (spamCountArray[english] + hamCountArray[english]));
                }
                // multiply with ham and spam probability it's corresponding probabilities
                hamProb *= hmValue / (hmValue + spValue);
                spamProb *= spValue / (spValue + hmValue);

            }
            // return ham if ham probability is more
            if (hamProb >= spamProb) {
                return Message.NOT_SPAM;
            } else {
                return Message.SPAM;
            }
        }
        // classify using hindi  model if language is hindi
        else if(lang.equals("Hindi")){

            String[] msgWords = MessageCleaning.HindiMessageCleaning(message.toLowerCase()).split("\\s+");//message.split("[\\s|;|:|,|)|(|{|}|[|]|/| |-]+");
            double hamProb = hamCountArray[hindi] * 1.0 / (hamCountArray[hindi] + spamCountArray[hindi]);
            double spamProb = spamCountArray[hindi] * 1.0 / (spamCountArray[hindi] + hamCountArray[hindi]);
            double v1,v2;
            for (String s : msgWords) {
                // get probability if exist in table
                if (spamWordsArray[hindi].containsKey(s)) {
                    v1 = spamWordsArray[hindi].get(s);
                } else {
                    // take constant probability if not exist
                    v1 = (1.0 / spamCountArray[hindi]);
                }

                // get probability if exist in table
                if (hamWordsArray[hindi].containsKey(s)) {
                    v2 = hamWordsArray[hindi].get(s);
                } else {
                    // take constant probability if not exist
                    v2 = (0.01 / hamCountArray[hindi]);
                }
                // multiply with ham and spam probability it's corresponding probabilities
                hamProb *= v2 / (v1 + v2);
                spamProb *= v1 / (v1 + v2);

            }
            // return ham if ham probability is more
            if (hamProb > spamProb) {
                Log.e("filter", "ham " + hamProb + " " + spamProb + " " + message);
                return Message.NOT_SPAM;
            } else {
                Log.e("filter", "spam " + hamProb + " " + spamProb + " " + message);
                return Message.SPAM;
            }
        }
        // classifiy all others in ham
        else
            return Message.NOT_SPAM;

    }

    /**
     * to classify list of message
     *
     * @param dataSet list of all messages which want to classify
     * @return return list of classified messages with ids
     * @throws IOException return exception if not get files
     */
    public HashMap<Integer, Integer> classify_all(HashMap<Integer, String> dataSet) throws IOException {

//        fillTable(Spam, Ham);
        // fillTableHindi(Spam,Ham);

        HashMap<Integer, Integer> spam_or_ham = new HashMap<>();

        Set<Integer> keys = dataSet.keySet();
        // go through all keys
        for (int key : keys) {
            String message = dataSet.get(key).toLowerCase();
            spam_or_ham.put(key, classify(message));
        }
        //StoreClassifier("english");
        return spam_or_ham;
    }

}