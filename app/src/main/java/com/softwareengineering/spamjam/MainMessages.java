package com.softwareengineering.spamjam;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

    static HashMap<Integer, Message> id_to_messages = new HashMap<>();

    static HashMap<Integer, String> spam_messages_training = new HashMap<>();
    static HashMap<Integer, String> ham_messages_training = new HashMap<>();
    static HashMap<Integer, String> messages_dataSet = new HashMap<>();

    static HashMap<Integer, Integer> messages_classified = new HashMap<>();

    static List<Integer> id_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messages);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainMessages.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

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
        messages_classified.putAll(Classifier.classify(spam_messages_training, ham_messages_training, messages_dataSet));

        Log.e("messages", spam_messages_training.toString());
        for (int key : keys){
            if(messages_classified.get(key) == Message.SPAM) {
                Log.e("messages", "Spam : " + key);
            }
        }
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

        ArrayAdapter<String> arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, messages_list.toArray());

        ListView listView = (ListView) findViewById(R.id.all_messages);
        listView.setAdapter(arrayAdapter);
        registerForContextMenu(listView);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.e("Error", "removing item pos=" + info.position);

        switch (item.getItemId()) {
            case HARDCODE_AS_HAM:
                if (!ham_messages_training.containsKey(id_list.get(info.position))) {
                    id_to_messages.get(id_list.get(info.position)).hard_coded = Message.YES;
                    id_to_messages.get(id_list.get(info.position)).spam = Message.NOT_SPAM;
                    ham_messages_training.put(id_list.get(info.position), id_to_messages.get(id_list.get(info.position)).message);
                    if (spam_messages_training.containsKey(id_list.get(info.position))) {
                        spam_messages_training.remove(id_list.get(info.position));
                    }
                    if (messages_dataSet.containsKey(id_list.get(info.position))) {
                        messages_dataSet.remove(id_list.get(info.position));
                    }
                    classify();
                }
                break;
            case HARDCODE_AS_SPAM:
                if (!spam_messages_training.containsKey(id_list.get(info.position))) {
                    id_to_messages.get(id_list.get(info.position)).hard_coded = Message.YES;
                    id_to_messages.get(id_list.get(info.position)).spam = Message.SPAM;
                    spam_messages_training.put(id_list.get(info.position), id_to_messages.get(id_list.get(info.position)).message);
                    if (ham_messages_training.containsKey(id_list.get(info.position))) {
                        ham_messages_training.remove(id_list.get(info.position));
                    }
                    if (messages_dataSet.containsKey(id_list.get(info.position))) {
                        messages_dataSet.remove(id_list.get(info.position));
                    }
                    classify();
                }
                break;
            case UNMARK:
                if(spam_messages_training.containsKey(id_list.get(info.position))) {
                    spam_messages_training.remove(id_list.get(info.position));
                }
                if(ham_messages_training.containsKey(id_list.get(info.position))) {
                    ham_messages_training.remove(id_list.get(info.position));
                }
                if(!messages_dataSet.containsKey(id_list.get(info.position))) {
                    messages_dataSet.put(id_list.get(info.position), id_to_messages.get(id_list.get(info.position)).message);
                }
                classify();
                break;
            //default:
            //    return super.onContextItemSelected(item);
        }

        return true;
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
