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


    public NBC_Classifier(Context ctx)
    {
        context = ctx;
        for (int i = 0; i < size; i++) {
            spamCountArray[i] = 0;
            hamCountArray[i] = 0;
            spamWordsArray[i] = new Hashtable<String, Double>();
            hamWordsArray[i] = new Hashtable<String, Double>();
        }

        load_classifier();

    }

    private void load_classifier() {
        try {
            File english_model = new File(context.getFilesDir(), "english_model");
            File hindi_model = new File(context.getFilesDir(), "hindi_model");
            if (english_model.exists() && hindi_model.exists()) {
                Scanner scanner = new Scanner(new FileReader(english_model));

                while (scanner.hasNextLine()) {
                    String word = scanner.next();
                    Double spamValue = scanner.nextDouble();
                    Double hamValue = scanner.nextDouble();
                    if (spamValue != 0) {
                        spamWordsArray[english].put(word, spamValue);
                    }
                    if (hamValue != 0) {
                        hamWordsArray[english].put(word, hamValue);
                    }
                    //Log.d("savedloading",word+" "+spamValue+" "+hamValue);

                }
                scanner.close();

                scanner = new Scanner(new FileReader(hindi_model));

                while (scanner.hasNextLine()) {
                    String word = scanner.next();
                    Double spamValue = scanner.nextDouble();
                    Double hamValue = scanner.nextDouble();

                    if (spamValue != 0) {
                        spamWordsArray[hindi].put(word, spamValue);
                    }
                    if (hamValue != 0) {
                        hamWordsArray[hindi].put(word, hamValue);
                    }
                }
            } else {
                fillTable(new HashMap<Integer, String>(), new HashMap<Integer, String>());
            }
            Log.d("debug", "loading classifer");
            Log.d("debug", "Spam count english : " + spamCountArray[english]);
            Log.d("debug", "ham count english : " + hamCountArray[english]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readDataSetFromFileInTable(Context ctx)
    {
        int EnglishId = R.raw.dataset_english;
        InputStream inputStream = ctx.getResources().openRawResource(EnglishId);

        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String word = scanner.next();
            int spamValue = scanner.nextInt();
            int hamValue = scanner.nextInt();
            if (spamValue != 0) {
                spamWordsArray[english].put(word, spamValue * 1.0);
                spamCountArray[english] += spamValue;
            }
            if (hamValue != 0) {
                hamWordsArray[english].put(word, hamValue * 1.0);
                hamCountArray[english] += hamValue;
            }
        }

        int HindiId = R.raw.dataset_hindi;

        inputStream = ctx.getResources().openRawResource(HindiId);
        scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            String word = scanner.next();
            int spamValue = scanner.nextInt();
            int hamValue = scanner.nextInt();
            if (spamValue != 0) {
                spamWordsArray[hindi].put(word, spamValue * 1.0);
                spamCountArray[hindi] += spamValue;
            }
            if (hamValue != 0) {
                hamWordsArray[hindi].put(word, hamValue * 1.0);
                hamCountArray[hindi] += hamValue;
            }
        }

        scanner.close();
    }


    public void fillTable(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham)  throws IOException
    {
        String message;
        for (int i = 0; i < size; i++) {
            spamWordsArray[i].clear();
            hamWordsArray[i].clear();
            spamCountArray[i] = 0;
            hamCountArray[i] = 0;
        }


        readDataSetFromFileInTable(context);

        Log.e("abcd", "spam count = " + spamCountArray[english]);
        Log.e("abcd", "ham count = " + hamCountArray[english]);

        Set<Integer> keys = Ham.keySet();
        for (int key : keys){
            message = Ham.get(key).toLowerCase();
            String lang = Language_Filter.predictor(message);

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
            } else if (lang.equals("Hindi")) {
                String[] msgWords = MessageCleaning.HindiMessageCleaning(message.toLowerCase()).split("\\s+");//message.split("[\\s|;|:|,|)|(|{|}|[|]|/| |-|\n]+");

                hamCountArray[hindi] += msgWords.length;

                for (String s : msgWords) {
                    if (!hamWordsArray[hindi].containsKey(s)) {
                        hamWordsArray[hindi].put(s, 1.0);
                    } else {
                        hamWordsArray[hindi].put(s, hamWordsArray[hindi].get(s) + 1.0);
                    }
                }
            }
        }

        keys = Spam.keySet();
        for (int key : keys){
            message = Spam.get(key).toLowerCase();
            String lang = Language_Filter.predictor(message);

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


    public void saveClassifier() throws IOException
    {
        File f = new File(context.getFilesDir(), "english_model");
        FileWriter fw = new FileWriter(f);
        Set<String> keys = spamWordsArray[english].keySet();
        Double value;
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

        keys = hamWordsArray[english].keySet();
        for (String key : keys) {
            eng.append(key + "\t" + "0.0" + "\t" + hamWordsArray[english].get(key) + "\n");
        }
        fw.write(eng.toString().substring(0, eng.length() - 1));
        fw.close();


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

        for (int i = 0; i < size; i++) {
            spamWordsArray[i].clear();
            hamWordsArray[i].clear();
        }


    }

    public int classify(String message)
    {
        message = message.toLowerCase();
        String lang = Language_Filter.predictor(message);
        if(lang.equals("English")){
            message = MessageCleaning.newWordCleaning(message);//MessageCleaning.messageCleaning(message);
            String[] msgWords = message.split("\\s+");
            double hamProb = hamCountArray[english] * 1.0 / (hamCountArray[english] + spamCountArray[english]);
            double spamProb = spamCountArray[english] * 1.0 / (spamCountArray[english] + hamCountArray[english]);
            double spValue, hmValue;
            for (String s : msgWords) {
                if (spamWordsArray[english].containsKey(s)) {
                    spValue = spamWordsArray[english].get(s);
                } else {
                    spValue = (1.0 / (spamCountArray[english] + hamCountArray[english]));
                }


                if (hamWordsArray[english].containsKey(s)) {
                    hmValue = hamWordsArray[english].get(s);
                } else {
                    hmValue = (1.0 / (spamCountArray[english] + hamCountArray[english]));
                }

                hamProb *= hmValue / (hmValue + spValue);
                spamProb *= spValue / (spValue + hmValue);

            }
            if (hamProb >= spamProb) {
                return Message.NOT_SPAM;
            } else {
                return Message.SPAM;
            }
        }
        else if(lang.equals("Hindi")){

            String[] msgWords = MessageCleaning.HindiMessageCleaning(message.toLowerCase()).split("\\s+");//message.split("[\\s|;|:|,|)|(|{|}|[|]|/| |-]+");
            double hamProb = hamCountArray[hindi] * 1.0 / (hamCountArray[hindi] + spamCountArray[hindi]);
            double spamProb = spamCountArray[hindi] * 1.0 / (spamCountArray[hindi] + hamCountArray[hindi]);
            double v1, v2;
            for (String s : msgWords) {
                if (spamWordsArray[hindi].containsKey(s)) {
                    v1 = spamWordsArray[hindi].get(s);
                } else {
                    v1 = (1.0 / spamCountArray[hindi]);
                }


                if (hamWordsArray[hindi].containsKey(s)) {
                    v2 = hamWordsArray[hindi].get(s);
                } else {
                    v2 = (0.01 / hamCountArray[hindi]);
                }
                hamProb *= v2 / (v1 + v2);
                spamProb *= v1 / (v1 + v2);

            }

            if (hamProb > spamProb) {
                Log.e("filter", "ham " + hamProb + " " + spamProb + " " + message);
                return Message.NOT_SPAM;
            } else {
                Log.e("filter", "spam " + hamProb + " " + spamProb + " " + message);
                return Message.SPAM;
            }
        }
        else
            return Message.SPAM;

    }

    public HashMap<Integer, Integer> classify_all(HashMap<Integer, String> dataSet) throws IOException {

//        fillTable(Spam, Ham);
        // fillTableHindi(Spam,Ham);

        HashMap<Integer, Integer> spam_or_ham = new HashMap<>();

        Set<Integer> keys = dataSet.keySet();
        for (int key : keys) {
            //Log.e("Red", key + " : " + dataSet.get(key));
            String message = dataSet.get(key).toLowerCase();
            spam_or_ham.put(key, classify(message));
        }
        //StoreClassifier("english");
        return spam_or_ham;
    }

}