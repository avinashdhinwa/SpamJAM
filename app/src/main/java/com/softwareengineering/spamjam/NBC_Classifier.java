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
    int size = 2;
    Hashtable<String,Double> spamWordsArray[] = new Hashtable[size];
    Hashtable<String,Double> hamWordsArray[] = new Hashtable[size];
    int spamCountArray[] = new int[size];
    int hamCountArray[] = new int[size];
    int english = 0;
    int hindi = 1;

/*    Hashtable<String,Double> spamWordsEng = new Hashtable<>();
    Hashtable<String,Double> hamWordsEng = new Hashtable<>();
    int spamCountEng = 0;
    int hamCountEng = 0;

    Hashtable<String,Double> spamWordsHindi = new Hashtable<>();
    Hashtable<String,Double> hamWordsHindi = new Hashtable<>();
    int spamCountHindi = 0;
    int hamCountHindi = 0;*/

    public  NBC_Classifier(Context ctx, SQLiteDatabase mydatabase)
    {
        context = ctx;
        load_classifier(mydatabase);
        for(int i = 0; i< size;i++){
            spamCountArray[i] = 0;
            hamCountArray[i] = 0;
            spamWordsArray[i] = new Hashtable<String, Double>();
            hamWordsArray[i] = new Hashtable<String, Double>();
        }
    }


    public void load_classifier(SQLiteDatabase mydatabase) {

    }

    }


    public void readDataSetFromFileInTable(Context ctx)
    {
        int EnglishId = R.raw.dataset_english;
        InputStream inputStream = ctx.getResources().openRawResource(EnglishId);

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
                    spamWordsArray[english].put(spliter[0],spamValue*1.0);
                    spamCountArray[english] += spamValue;
                }
                if(hamValue != 0){
                    hamWordsArray[english].put(spliter[0],hamValue*1.0);
                    hamCountArray[english] += hamValue;
                }
            }
            bufferedreader.close();
        }
        catch (IOException e)
        {
            Log.e("Exception",e.getMessage());
        }

        int HindiId = R.raw.dataset_hindi;

        inputStream = ctx.getResources().openRawResource(HindiId);

        inputreader = new InputStreamReader(inputStream);
        bufferedreader = new BufferedReader(inputreader);
        try
        {
            while (( line = bufferedreader.readLine()) != null) {
                String spliter[] = line.split("\t");
               // Log.e("mylogvalue",line+" "+spliter.length);
                int spamValue = Integer.parseInt(spliter[1]);
                int hamValue = Integer.parseInt(spliter[2]);
                if(spamValue != 0){
                    spamWordsArray[hindi].put(spliter[0],spamValue*1.0);
                    spamCountArray[hindi] += spamValue;
                }
                if(hamValue != 0){
                    hamWordsArray[hindi].put(spliter[0],hamValue*1.0);
                    hamCountArray[hindi] += hamValue;
                }
            }
            bufferedreader.close();
        }
        catch (IOException e)
        {
            Log.e("lines",e.getMessage()+" errorMessage");
        }


    }

    /*public void readDataSetHindiTable(Context ctx, int resId)
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
    }*/
    public void fillTable(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham)  throws IOException
    {
        String message;
        for(int i =0; i < size;i++){
            spamWordsArray[i].clear();
            hamWordsArray[i].clear();
            spamCountArray[i] = 0;
            hamCountArray[i] = 0;
        }


        readDataSetFromFileInTable(context);
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
            }


           else if(lang.equals("Hindi")){
                String[] msgWords = message.split("[\\s|;|:|,|)|(|{|}|[|]|\n]+");

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
            }
            else if(lang.equals("Hindi")){
                String[] msgWords = message.split("[\\s|;|:|,|)|(|{|}|[|]|\n]+");

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

        for(int i = 0;i < size;i++) {
            Set<String> keySet = hamWordsArray[i].keySet();
            for (String s : keySet) {
                hamWordsArray[i].put(s, hamWordsArray[i].get(s) / hamCountArray[i]);
            }

            keySet = spamWordsArray[i].keySet();
            for (String s : keySet) {
                spamWordsArray[i].put(s, spamWordsArray[i].get(s) / spamCountArray[i]);
            }
        }

    }


    /*public void saveClassifier(String language) throws IOException
    {
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
    }*/

    public int classify(String message)
    {
        String lang = Language_Filter.predictor(message);

        Log.e("classifier",lang);
        if(lang.equals("English")){
            message = MessageCleaning.newWordCleaning(message);//MessageCleaning.messageCleaning(message);
            String[] msgWords = message.split("\\s+");
            double hamProb = hamCountArray[english] * 1.0 / (hamCountArray[english] + spamCountArray[english]);
            double spamProb = spamCountArray[english] * 1.0 / (spamCountArray[english] + hamCountArray[english]);

            for (String s : msgWords) {
                double spValue,hmValue;
                if (spamWordsArray[english].containsKey(s)) {
                    spValue= spamWordsArray[english].get(s);
                } else {
                    spValue = (1.0 / spamCountArray[english]);
                }


                if (hamWordsArray[english].containsKey(s)) {
                    hmValue = hamWordsArray[english].get(s);
                } else {
                    hmValue = (1.0 / hamCountArray[english]);
                }
                spamProb *= spValue/(spValue+hmValue);
                hamProb *= hmValue/(spValue+hmValue);


            }

            if (hamProb > spamProb)
                return Message.NOT_SPAM;
            else
                return Message.SPAM;
        }
        else if(lang.equals("Hindi")){

            String[] msgWords = message.split("\\s+");
            double hamProb = hamCountArray[hindi] * 1.0 / (hamCountArray[hindi] + spamCountArray[hindi]);
            double spamProb = spamCountArray[hindi] * 1.0 / (spamCountArray[hindi] + hamCountArray[hindi]);

            for (String s : msgWords) {
                if (spamWordsArray[hindi].containsKey(s)) {
                    spamProb *= spamWordsArray[hindi].get(s);
                } else {
                    spamProb *= (1.0 / spamCountArray[hindi]);
                }


                if (hamWordsArray[hindi].containsKey(s)) {
                    hamProb *= hamWordsArray[hindi].get(s);
                } else {
                    hamProb *= (1.0 / hamCountArray[hindi]);
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