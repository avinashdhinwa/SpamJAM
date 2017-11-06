package com.softwareengineering.spamjam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Language_Filter {

    //LinkedList<String> list = new LinkedList<>();

    public static ArrayList<String> languages =  new ArrayList<>(Arrays.asList("English",
                                                                                    "Hindi",
                                                                                    "Punjabi",
                                                                                    "Gujarati",
                                                                                    "Malayalam"));

    private HashMap<String , Integer> map = new HashMap<>();

    private LinkedList ReadMsg(LinkedList<String> list){
       LinkedList<String> pred = new LinkedList<>();

        for(int i=0;i<list.size();i++) {
            pred.add(predictor(list.get(i)));
            map.put("Hindi",0);
            map.put("English",0);
            map.put("Gujarati",0);
            map.put("Punjabi",0);
            map.put("Malayalam",0);
        }

        return pred;
    }

    private String predictor(String Msg){
        int sum=0;
        for(int i=0;i<Msg.length();i++){
            if((int)Msg.charAt(i)>=2309 && (int)Msg.charAt(i)<2431){
                map.put("Hindi", map.get("Hindi") + 1);
            }
            if((int)Msg.charAt(i)>=2688 && (int)Msg.charAt(i)<2815){
                map.put("Gujarati", map.get("Gujarati") + 1);
            }
            if((int)Msg.charAt(i)>=2560 && (int)Msg.charAt(i)<2687){
                map.put("Punjabi", map.get("Punjabi") + 1);
            }
            if((int)Msg.charAt(i)>=2432 && (int)Msg.charAt(i)<2559){
                map.put("English", map.get("English") + 1);
            }
            if((int)Msg.charAt(i)>=3328 && (int)Msg.charAt(i)<3455){
                map.put("Malyalam", map.get("Malyalam") + 1);
            }
        }

        int max=0;
        String lang="";
        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            int x = entry.getValue();
            if(x>max) {
                max = x;
                lang = entry.getKey();
            }

        }

        return lang;
    }

    public Language_Filter(){
        languages.add("English");
        languages.add("Hindi");
        languages.add("Punjabi");
        languages.add("Gujarati");
        languages.add("Malayalam");
    }

}
