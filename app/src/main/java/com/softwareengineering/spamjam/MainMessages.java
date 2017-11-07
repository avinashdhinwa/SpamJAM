package com.softwareengineering.spamjam;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MainMessages extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int HARDCODE_AS_SPAM = 111;
    private static final int HARDCODE_AS_HAM = 121;
    private static final int UNMARK = 131;

    private static int SHOWING_SPAM_OR_HAM = Message.NOT_SPAM;
    private static final int TOOLBAR_SETTINGS = 201;
    private static final int TOOLBAR_SPAM = 202;
    private static final int TOOLBAR_NON_SPAM = 203;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    static HashMap<Integer, Message> id_to_messages = new HashMap<>();

    static HashMap<Integer, String> spam_messages_training = new HashMap<>();
    static HashMap<Integer, String> ham_messages_training = new HashMap<>();
    static HashMap<Integer, String> messages_dataSet = new HashMap<>();

    static HashMap<Integer, Integer> messages_classified = new HashMap<>();

    static List<Integer> id_list = new ArrayList<>();
    static List<Integer> id_list_spam = new ArrayList<>();
    static List<Integer> id_list_non_spam = new ArrayList<>();

    static List<String> spam_messages_list = new ArrayList<>();
    static List<String> non_spam_messages_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainMessages.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        listView = (ListView) findViewById(R.id.all_messages);
        listView.setAdapter(arrayAdapter);
        registerForContextMenu(listView);

        //read_classified_messages();

        readMessages();

    }

    private void classify() {

        Set<Integer> keys = id_to_messages.keySet();
        for (int key : keys) {
            if(id_to_messages.get(key).hard_coded == Message.YES){
                if(id_to_messages.get(key).spam == Message.SPAM){
                    spam_messages_training.put(key, id_to_messages.get(key).message);
                }
                else{
                    ham_messages_training.put(key, id_to_messages.get(key).message);
                }
                messages_classified.put(key, id_to_messages.get(key).spam);
            }
            else{
                messages_dataSet.put(key, id_to_messages.get(key).message);
            }
        }
        try {
            messages_classified.putAll(NBC_Classifier.classify(spam_messages_training, ham_messages_training, messages_dataSet));
        } catch (IOException e) {
            Log.e("Error", "File not found");
            e.printStackTrace();
        }

//        Log.e("messages", spam_messages_training.toString());

        Log.e("filling layout", "Generating data for layout");

        id_list_non_spam.clear();
        id_list_spam.clear();
        spam_messages_list.clear();
        non_spam_messages_list.clear();
        for (int key : keys){
            if(messages_classified.get(key) == Message.SPAM) {
                id_list_spam.add(key);
                spam_messages_list.add(id_to_messages.get(key).message);
                Log.e("messages", "Spam : " + key);
            }
            else if(messages_classified.get(key) == Message.NOT_SPAM) {
                id_list_non_spam.add(key);
                non_spam_messages_list.add(id_to_messages.get(key).message);
            }
        }

        fill_the_layout_with_messages();
    }

    private void read_classified_messages() {
        File file = new File(this.getFilesDir(), "messages_classes.txt");

        id_to_messages = new HashMap<>();
        try {
            Scanner sc = new Scanner(new FileInputStream(file));

            while (sc.hasNext()) {
                int id = sc.nextInt();
                id_to_messages.put(id, new Message(id, sc.nextInt(), sc.nextInt()));
//                Log.e("Reading : ", id_to_messages.get(id).to_string_for_file());
                if(id_to_messages.get(id).hard_coded == Message.YES){
                    Log.e("Error", "got a hardcoded message --> " + id);
                }
            }

        } catch (FileNotFoundException e) {
            Log.e("Error", "Never classified yet");
            e.printStackTrace();
        }

        id_to_messages.clear();
        Log.d("read from file", id_to_messages.toString());
    }

    void readMessages() {
        String INBOX = "content://sms/inbox";

        Cursor cursor = getContentResolver().query(Uri.parse(INBOX), null, null, null, null);
        List<String> messages_list = new ArrayList<>();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                /*String msgData = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }*/

                //String msgData += cursor.getColumnName(13) + ":" + cursor.getString(13);
                String msgData = cursor.getString(13);

                messages_list.add(msgData);
                //messages_list.add(cursor.getInt(0) + " : " + msgData);
                id_list.add(cursor.getInt(0));

                // use msgData
            } while (cursor.moveToNext());
        } else {
            Log.e("Error", "no messages");
        }

        for (int i = 0; i < messages_list.size(); i++) {
            if (id_to_messages.containsKey(id_list.get(i))) {
                id_to_messages.get(id_list.get(i)).set_message(messages_list.get(i));
            } else {
                id_to_messages.put(id_list.get(i), new Message(messages_list.get(i), id_list.get(i)));
            }
        }

        if(spam_messages_list.isEmpty() && non_spam_messages_list.isEmpty()){
            classify();
        }

        arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, non_spam_messages_list.toArray());
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.e("Error", "removing item pos=" + info.position);

        int id_from_pos = 0;
        if(SHOWING_SPAM_OR_HAM == Message.SPAM){
            id_from_pos = id_list_spam.get(info.position);
        }
        else if(SHOWING_SPAM_OR_HAM == Message.NOT_SPAM){
            id_from_pos = id_list_non_spam.get(info.position);
        }

        switch (item.getItemId()) {
            case HARDCODE_AS_HAM:
                if (!ham_messages_training.containsKey(id_from_pos)) {
                    id_to_messages.get(id_from_pos).hard_coded = Message.YES;
                    id_to_messages.get(id_from_pos).spam = Message.NOT_SPAM;
                    ham_messages_training.put(id_from_pos, id_to_messages.get(id_from_pos).message);
                    if (spam_messages_training.containsKey(id_from_pos)) {
                        spam_messages_training.remove(id_from_pos);
                    }
                    if (messages_dataSet.containsKey(id_from_pos)) {
                        messages_dataSet.remove(id_from_pos);
                    }
                    classify();
                }
                break;
            case HARDCODE_AS_SPAM:
                if (!spam_messages_training.containsKey(id_from_pos)) {
                    id_to_messages.get(id_from_pos).hard_coded = Message.YES;
                    id_to_messages.get(id_from_pos).spam = Message.SPAM;
                    spam_messages_training.put(id_from_pos, id_to_messages.get(id_from_pos).message);
                    if (ham_messages_training.containsKey(id_from_pos)) {
                        ham_messages_training.remove(id_from_pos);
                    }
                    if (messages_dataSet.containsKey(id_from_pos)) {
                        messages_dataSet.remove(id_from_pos);
                    }
                    classify();
                }
                break;
            case UNMARK:
                if(spam_messages_training.containsKey(id_from_pos)) {
                    spam_messages_training.remove(id_from_pos);
                }
                if(ham_messages_training.containsKey(id_from_pos)) {
                    ham_messages_training.remove(id_from_pos);
                }
                if(!messages_dataSet.containsKey(id_from_pos)) {
                    messages_dataSet.put(id_from_pos, id_to_messages.get(id_from_pos).message);
                }
                classify();
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.e("Error", "long click");
        if (v.getId() == R.id.all_messages) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            menu.setHeaderTitle("Options");
            String[] menuItems = {"Mark as Spam", "Mark as Non-Spam", "Unmark"};
            menu.add(Menu.NONE, HARDCODE_AS_SPAM, 0, menuItems[0]);
            menu.add(Menu.NONE, HARDCODE_AS_HAM, 0, menuItems[1]);
            menu.add(Menu.NONE, UNMARK, 0, menuItems[2]);
        }
    }

    private void fill_the_layout_with_messages() {

        switch(SHOWING_SPAM_OR_HAM){
            case Message.NOT_SPAM:{
                arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, non_spam_messages_list.toArray());
                listView.setAdapter(arrayAdapter);
                Log.e("filling layout", "Displaying NOT SPAM");
            }
            break;
            case Message.SPAM:{
                arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, spam_messages_list.toArray());
                listView.setAdapter(arrayAdapter);
                Log.e("filling layout", "Displaying SPAM");
            }
            break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case TOOLBAR_SETTINGS:{
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
            }
            break;
            case TOOLBAR_NON_SPAM:{
                SHOWING_SPAM_OR_HAM = Message.NOT_SPAM;
                invalidateOptionsMenu();
                fill_the_layout_with_messages();
            }
            break;
            case TOOLBAR_SPAM:{
                SHOWING_SPAM_OR_HAM = Message.SPAM;
                invalidateOptionsMenu();
                fill_the_layout_with_messages();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(SHOWING_SPAM_OR_HAM == Message.SPAM) {
            menu.add(0, TOOLBAR_NON_SPAM, 0, "Inbox");
            menu.getItem(0).setIcon(R.drawable.ic_inbox_black_24dp);
        }
        else{
            menu.add(0, TOOLBAR_SPAM, 0, "Spam");
            menu.getItem(0).setIcon(R.drawable.ic_report_black_24dp);
        }

        menu.add(0, TOOLBAR_SETTINGS, 1, "Settings");
        menu.getItem(1).setIcon(R.drawable.ic_settings_black_24dp);

//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.toolbar_menu, menu);;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("Error", "writing to file started");
        File file = new File(this.getFilesDir(), "messages_classes.txt");

        try {
            file.createNewFile();
            OutputStreamWriter myOutWriter = new OutputStreamWriter(new FileOutputStream(file));

            Set<Integer> keys = id_to_messages.keySet();
            for (int key : keys) {
                //Log.e("Writing : ", id_to_messages.get(key).to_string_for_file());
                myOutWriter.append(id_to_messages.get(key).to_string_for_file());
            }

            myOutWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        Log.e("Error", "writing to file ends");

    }
}
