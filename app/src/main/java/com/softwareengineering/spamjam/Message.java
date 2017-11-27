package com.softwareengineering.spamjam;

import java.util.Calendar;

class Message {

    static final int YES = 1;
    static final int NO = -1;
    static final int SPAM = 1;
    static final int NOT_SPAM = -1;

    static final int INBOX = 0;

    static private Calendar calendar = Calendar.getInstance();

    // id of a message (It is unique for each message)
    int id;
    // sender's id in the saved contacts (null if not present)
    String person;
    // main content of the message
    String body;
    // sender's id (to which we can reply) which the telecoms know
    String address;
    // date and time of receiving message hh:mm dd-mm-yyyy
    String date;

    // tells whether message is from 'sent' or 'inbox'
    int message_type;

    // determines whether this message is part of training dataset or not
    int hard_coded;
    // indicates whether message is Spam or not
    int spam;

    /**
     * @param id      id of a message
     * @param body    body of a message
     * @param person  sender's id from contacts
     * @param address sender's id (to which we can reply) which the telecoms know
     * @param date    date of recieving message
     */
    public Message(int id, String body, String person, String address, String date){
        this.id = id;
        this.body = body;
        this.person = person;
        this.address = address;
        this.date = date;
        this.hard_coded = NOT_SPAM;
        this.spam = NOT_SPAM;
    }

    /**
     * @param id         id of message
     * @param hard_coded determines whether this message is part of training dataset or not
     * @param spam       shows whether message is spam or not
     */
    public Message(int id, int hard_coded, int spam){
        this.id = id;
        this.hard_coded = hard_coded;
        this.spam = spam;
    }

    /** Converts time from millis to hh:mm dd-mm-yyyy
     *
     * @param millis time in milliseconds
     * @return tile in hh:mm dd-mm-yyyy
     */
    public static String millisToTime(long millis) {

        StringBuilder stringBuilder = new StringBuilder();

        calendar.setTimeInMillis(millis);
        stringBuilder = stringBuilder.append(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))).append(":").
                append(String.format("%02d", calendar.get(Calendar.MINUTE))).append(" ").
                append(String.format("%02d", calendar.get(Calendar.DATE))).append("-").
                append(String.format("%02d", calendar.get(Calendar.MONTH))).append("-").
                append(calendar.get(Calendar.YEAR));

        return stringBuilder.toString();
    }

    /** Set's body, person, address and date of message (having just a message_id)
     *
     * @param body body of message
     * @param person sender's id according to contacts database
     * @param address sender's address (might be a number or some text(which is provided by telecoms))
     * @param date date and time of receiving message
     */
    public void set_message(String body, String person, String address, String date) {
        this.body = body;
        this.person = person;
        this.address = address;
        this.date = date;
    }

    /**
     * @return returns string from message, which is needed to write in a file
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

    public void setMessageType(int folder_type) {
        this.message_type = folder_type;
    }
}