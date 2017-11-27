package com.softwareengineering.spamjam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LanguageFilter {

    static String ENGLISH = "English";
    static String HINDI = "Hindi";
    static String PUNJABI = "Punjabi";
    static String GUJARATI = "Gujarati";
    static String MALAYALAM = "Malayalam";
    /**
     * list of languages
     */
    public static ArrayList<String> languages =  new ArrayList<>(Arrays.asList(ENGLISH,
                                                                                    HINDI,
                                                                                    PUNJABI,
                                                                                    GUJARATI,
                                                                                    MALAYALAM));

    static HashMap<String , Integer> map = new HashMap<>();

    public LanguageFilter() {

    }

    /**
     * method will return language of given given message
     *
     * @param Msg given message string
     * @return language of message
     */
    public static String predictor(String Msg){
        int sum = 0;
        // put count for all language 0 initially
        map.put("Hindi",0);
        map.put("English",0);
        map.put("Gujarati",0);
        map.put("Punjabi",0);
        map.put("Malayalam", 0);
        // go through all message character
        for (int i = 0; i < Msg.length(); i++) {

            // if it is white space skip it otherwise add in corresponding language
            if ((int) Msg.charAt(i) == 32) {
                continue;
            } else if ((int) Msg.charAt(i) >= 0 && (int) Msg.charAt(i) < 127) {
                map.put("English", map.get("English") + 1);
            } else if ((int) Msg.charAt(i) >= 2304 && (int) Msg.charAt(i) < 2431) {
                map.put("Hindi", map.get("Hindi") + 1);
            } else if ((int) Msg.charAt(i) >= 2688 && (int) Msg.charAt(i) < 2815) {
                map.put("Gujarati", map.get("Gujarati") + 1);
            } else if ((int) Msg.charAt(i) >= 2560 && (int) Msg.charAt(i) < 2687) {
                map.put("Punjabi", map.get("Punjabi") + 1);
            } else if ((int) Msg.charAt(i) >= 3328 && (int) Msg.charAt(i) < 3455) {
                map.put("Malyalam", map.get("Malyalam") + 1);
            }
        }

        // return the max value language s
        int max=0;
        String lang="";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            int x = entry.getValue();
            if(x>max) {
                max = x;
                lang = entry.getKey();
            }

        }
        return lang;
    }

}
