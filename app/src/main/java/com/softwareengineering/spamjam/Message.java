package com.softwareengineering.spamjam;

import java.util.Calendar;

/**
 * Created by Unknown User on 30-10-2017.
 */

public class Message {

    static final int YES = 1;
    static final int NO = -1;
    static final int SPAM = 1;
    static final int NOT_SPAM = -1;
    static Calendar calendar = Calendar.getInstance();
    int id;
    String person;
    String body;
    String address;
    String date;
    int hard_coded;
    int spam;

    public Message(int id, String body, String person, String address, String date){
        this.id = id;
        this.body = body;
        this.person = person;
        this.address = address;
        this.date = date;
        this.hard_coded = NOT_SPAM;
        this.spam = NOT_SPAM;
    }

    public Message(int id, int hard_coded, int spam){
        this.id = id;
        this.hard_coded = hard_coded;
        this.spam = spam;
    }

    public static String millisToTime(long millis){

        calendar.setTimeInMillis(millis);
        String date = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", calendar.get(Calendar.MINUTE)) + " " +
                String.format("%02d", calendar.get(Calendar.DATE)) + "-" +
                String.format("%02d", calendar.get(Calendar.MONTH)) + "-" +
                calendar.get(Calendar.YEAR);

        return date;
    }

    public void set_message(String body, String person, String address, String date) {
        this.body = body;
        this.person = person;
        this.address = address;
        this.date = date;
    }

    /**
     * @return
     */
    public String to_string_for_file(){
        return (id + " " + hard_coded + " " + spam + "\n");
    }

    /** For printing Message object while debugging
     *
     * @return concatenating data members of Message object for
     */
    public String to_string_for_debug(){ return ("From : " + address + " at " + date + "\n" + body + "\n");
    }

}
