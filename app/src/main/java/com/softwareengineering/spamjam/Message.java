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
    String message;
    int hard_coded;
    int spam;

    public Message(String message, int id){
        this.id = id;
        this.message = message;
        this.hard_coded = NOT_SPAM;
        this.spam = NOT_SPAM;
    }

    public Message(String message, int id, int hard_coded, int spam){
        this.id = id;
        this.message = message;
        this.hard_coded = hard_coded;
        this.spam = spam;
    }

    public Message(int id, int hard_coded, int spam){
        this.id = id;
        this.hard_coded = hard_coded;
        this.spam = spam;
    }

    public void set_message(String message){
        this.message = message;
    }

    public String to_string_for_file(){
        return (id + " " + hard_coded + " " + spam + "\n");
    }

}
