package com.softwareengineering.spamjam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Deepak on 04-11-2017.
 */

public class NBC_Classifier{

    Context context;
    Hashtable<String,Double> spamWordsEng = new Hashtable<>();
    Hashtable<String,Double> hamWordsEng = new Hashtable<>();
    int spamCountEng = 0;
    int hamCountEng = 0;

    Hashtable<String,Double> spamWordsHindi = new Hashtable<>();
    Hashtable<String,Double> hamWordsHindi = new Hashtable<>();
    int spamCountHindi = 0;
    int hamCountHindi = 0;

    public  NBC_Classifier(Context ctx, SQLiteDatabase mydatabase)
    {
        context = ctx;
        load_classifier(mydatabase);
    }

    public void load_classifier(SQLiteDatabase mydatabase) {

    }

    public void readDataSetInTable(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader bufferedreader = new BufferedReader(inputreader);
        String line;
        try
        {
            while (( line = bufferedreader.readLine()) != null) {
                String spliter[] = line.toLowerCase().split("\t");
                int spamValue = Integer.parseInt(spliter[1]);
                int hamValue = Integer.parseInt(spliter[2]);
                if(spamValue != 0){
                    spamWordsEng.put(spliter[0],spamValue*1.0);
                    spamCountEng += spamValue;
                }
                if(hamValue != 0){
                    hamWordsEng.put(spliter[0],hamValue*1.0);
                    hamCountEng += hamValue;
                }
            }
        }
        catch (IOException e)
        {
            Log.e("Exception",e.getMessage());
        }
    }

    public void readDataSetHindiTable(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader bufferedreader = new BufferedReader(inputreader);
        String line;
        try
        {
            while (( line = bufferedreader.readLine()) != null) {
                String spliter[] = line.split("\t");
                Log.e("mylogvalue",line+" "+spliter.length);
                int spamValue = Integer.parseInt(spliter[1]);
                int hamValue = Integer.parseInt(spliter[2]);
                if(spamValue != 0){
                    spamWordsHindi.put(spliter[0],spamValue*1.0);
                    spamCountHindi += spamValue;
                }
                if(hamValue != 0){
                    hamWordsHindi.put(spliter[0],hamValue*1.0);
                    hamCountHindi += hamValue;
                }
            }
        }
        catch (IOException e)
        {
            Log.e("lines",e.getMessage()+" errorMessage");
        }
        Log.e("lines","spamCount = "+spamCountHindi);
        Log.e("lines","hamCount = "+hamCountHindi);

    }

    public void fillTableHindi(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham)  throws IOException
    {
        String message;
        spamWordsHindi.clear();
        hamWordsHindi.clear();

        readDataSetHindiTable(context,R.raw.dataset_hindi);

        Log.e("lines", "ham Count = " + hamCountHindi);
        Log.e("lines", "spam Count = " + spamCountHindi);
        Set<Integer> keys = Ham.keySet();
        for (int key : keys){
            message = Ham.get(key);
            String lang = Language_Filter.predictor(message);

            if(lang.equals("Hindi")) {
                String[] msgWords = message.split("[\\s|;|:|,|)|(|{|}|[|]|\n]+");

                hamCountHindi += msgWords.length;

                for (String s : msgWords) {
                    if (!hamWordsHindi.containsKey(s)) {
                        hamWordsHindi.put(s, 1.0);
                    } else {
                        hamWordsHindi.put(s, hamWordsHindi.get(s) + 1.0);
                    }
                }
            }
        }

        keys = Spam.keySet();
        for (int key : keys){
            message = Spam.get(key);
            String lang = Language_Filter.predictor(message);

            if(lang.equals("Hindi")) {
                String[] msgWords = message.split("[\\s|;|:|,|)|(|{|}|[|]|\n]+");

                spamCountHindi += msgWords.length;

                for (String s : msgWords) {
                    if (!spamWordsHindi.containsKey(s)) {
                        spamWordsHindi.put(s, 1.0);
                    } else {
                        spamWordsHindi.put(s, spamWordsHindi.get(s) + 1);
                    }
                }
            }
        }

        Set<String> keySet = hamWordsHindi.keySet();
        for(String s: keySet)
        {
            hamWordsHindi.put(s, hamWordsHindi.get(s)/hamCountHindi);
        }

        keySet = spamWordsHindi.keySet();
        for(String s: keySet)
        {
            spamWordsHindi.put(s, spamWordsHindi.get(s)/spamCountHindi);
        }

        Log.e("lines", "ham Count Hindi = " + hamCountHindi);
        Log.e("lines", "spam Count Hindi  = " + spamCountHindi);
    }
    public void fillTable(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham)  throws IOException
    {
        String message;
        spamWordsEng.clear();
        hamWordsEng.clear();

        readDataSetInTable(context,R.raw.dataset_english);

        Log.e("lines", "ham Count = " + hamCountEng);
        Log.e("lines", "spam Count = " + spamCountEng);
        Set<Integer> keys = Ham.keySet();
        for (int key : keys){
            message = Ham.get(key).toLowerCase();
            String lang = Language_Filter.predictor(message);

            if(lang.equals("English")) {
                message = MessageCleaning.newWordCleaning(message);//MessageCleaning.messageCleaning(message);
                String[] msgWords = message.split("\\s+");//message.split("[\\\\s|\\\\t|.|/|,|:|!|'|=|+|\n|-]+");

                hamCountEng += msgWords.length;

                for (String s : msgWords) {
                    if (!hamWordsEng.containsKey(s)) {
                        hamWordsEng.put(s, 1.0);
                    } else {
                        hamWordsEng.put(s, hamWordsEng.get(s) + 1.0);
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

                spamCountEng += msgWords.length;

                for (String s : msgWords) {
                    if (!spamWordsEng.containsKey(s)) {
                        spamWordsEng.put(s, 1.0);
                    } else {
                        spamWordsEng.put(s, spamWordsEng.get(s) + 1);
                    }
                }
            }
        }

        Set<String> keySet = hamWordsEng.keySet();
        for(String s: keySet)
        {
            hamWordsEng.put(s, hamWordsEng.get(s)/hamCountEng);
        }

        keySet = spamWordsEng.keySet();
        for(String s: keySet)
        {
            spamWordsEng.put(s, spamWordsEng.get(s)/spamCountEng);
        }

        Log.e("lines", "ham Count = " + hamCountEng);
        Log.e("lines", "spam Count = " + spamCountEng);
    }

    public void saveClassifier() throws IOException
    {
        String language = "english";
        if(language.equals("english")){
            File f = new File(context.getFilesDir(),"english_model");
            FileWriter fw = new FileWriter(f);
            StringBuilder stringBuilder = new StringBuilder();

            Set<String> keys = spamWordsEng.keySet();

            Double value;
            for(String key: keys){
                if(hamWordsEng.containsKey(key)){
                    value = hamWordsEng.get(key);
                }
                else{
                    value = 0.0;
                }
                stringBuilder.append(key+"\t"+spamWordsEng.get(key)+"\t"+value+"\n");
            }

            keys = hamWordsEng.keySet();
            for(String key: keys){
                stringBuilder.append(key+"\t"+"0.0"+"\t"+hamWordsEng.get(key)+"\n");
            }

            String string = stringBuilder.toString().substring(0,stringBuilder.length()-1);
            fw.write(string);
            fw.close();
        }
    }

    public int classify(String message)
    {
        String lang = Language_Filter.predictor(message);

        Log.e("classifier",lang);
        if(lang.equals("English")){
            message = MessageCleaning.newWordCleaning(message);//MessageCleaning.messageCleaning(message);
            String[] msgWords = message.split("\\s+");
            double hamProb = hamCountEng * 1.0 / (hamCountEng + spamCountEng);
            double spamProb = spamCountEng * 1.0 / (spamCountEng + hamCountEng);

            for (String s : msgWords) {
                if (spamWordsEng.containsKey(s)) {
                    spamProb *= spamWordsEng.get(s);
                } else {
                    spamProb *= (1.0 / spamCountEng);
                }


                if (hamWordsEng.containsKey(s)) {
                    hamProb *= hamWordsEng.get(s);
                } else {
                    hamProb *= (1.0 / hamCountEng);
                }

                hamProb *= 4;
                spamProb *= 4;

            }

            if (hamProb > spamProb)
                return Message.NOT_SPAM;
            else
                return Message.SPAM;
        }
        else if(lang.equals("Hindi")){

            String[] msgWords = message.split("\\s+");
            double hamProb = hamCountHindi * 1.0 / (hamCountHindi + spamCountHindi);
            double spamProb = spamCountHindi * 1.0 / (spamCountHindi + hamCountHindi);

            for (String s : msgWords) {
                if (spamWordsHindi.containsKey(s)) {
                    spamProb *= spamWordsHindi.get(s);
                } else {
                    spamProb *= (1.0 / spamCountHindi);
                }


                if (hamWordsHindi.containsKey(s)) {
                    hamProb *= hamWordsHindi.get(s);
                } else {
                    hamProb *= (1.0 / hamCountHindi);
                }

            }
            Log.e("hindiMessage",message);

            if (hamProb >= spamProb)
                return Message.NOT_SPAM;
            else
                return Message.SPAM;
        }
        else
            return Message.SPAM;

    }

    public HashMap<Integer, Integer> classify_all(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham, HashMap<Integer, String> dataSet) throws IOException{

        fillTable(Spam, Ham);
        fillTableHindi(Spam,Ham);

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