package com.softwareengineering.spamjam;

/**
 * Created by Unknown User on 30-10-2017.
 */

public class Message {

    static final int YES = 1;
    static final int NO = -1;

    static final int SPAM = 1;
    static final int NOT_SPAM = -1;

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

    public Message(int id, String body, int hard_coded, int spam){
        this.id = id;
        this.body = body;
        this.hard_coded = hard_coded;
        this.spam = spam;
    }

    public Message(int id, int hard_coded, int spam){
        this.id = id;
        this.hard_coded = hard_coded;
        this.spam = spam;
    }

    public void set_message(String body, String person, String address, String date){
        this.body = body;
        this.person = person;
        this.address = address;
        this.date = date;
    }

    public String to_string_for_file(){
        return (id + " " + hard_coded + " " + spam + "\n");
    }

}
