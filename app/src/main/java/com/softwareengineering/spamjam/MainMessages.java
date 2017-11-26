package com.softwareengineering.spamjam;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MainMessages extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int HARDCODE_AS_SPAM = 111;
    private static final int HARDCODE_AS_HAM = 112;
    private static final int UNMARK = 113;
    private static final int DELETE = 114;
    private static final int BLACKLIST_CONTACT = 115;
    private static final int WHITELIST_CONTACT = 116;
    private static final int RETRAIN = 111;
    private static final int CLASSIFY_BY_ADDRESS_ONLY = 112;
    static Classifier classifier;
    static HashMap<Integer, Message> id_to_messages = new HashMap<>();
    static HashMap<Integer, String> spam_messages_training = new HashMap<>();
    static HashMap<Integer, String> ham_messages_training = new HashMap<>();
    static HashMap<Integer, String> messages_dataSet = new HashMap<>();
    static HashMap<Integer, Integer> messages_classified = new HashMap<>();
    static List<Integer> id_list = new ArrayList<>();
    static List<Integer> id_list_spam = new ArrayList<>();
    static List<Integer> id_list_non_spam = new ArrayList<>();
    private static int SHOWING_SPAM_OR_HAM = Message.NOT_SPAM;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FloatingActionButton fab;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    SmsBroadcastReceiver smsBroadcastReceiver;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handle_navigation_view();

        handle_floating_button();

        while (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainMessages.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        init();

    }

    /**
     * Floating Button Click Listener and start new Activity to send message
     */
    private void handle_floating_button() {


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SendMessages.class));
            }
        });


    }



    private void init() {

        classifier = new Classifier(this);

        attach_listener_on_listview();

//        read_classified_messages();
        readMessages();
    }

    private void attach_listener_on_listview() {

        listView = (ListView) findViewById(R.id.all_messages);
        registerForContextMenu(listView);

        load_message_in_new_activity();
    }

    private void load_message_in_new_activity() {



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                int pos = parent.getPositionForView(view);
                Message message = null;

                if(SHOWING_SPAM_OR_HAM == Message.SPAM){
                    message = id_to_messages.get(id_list_spam.get(pos));
                }
                else if(SHOWING_SPAM_OR_HAM == Message.NOT_SPAM){
                    message = id_to_messages.get(id_list_non_spam.get(pos));
                }

                //Toast.makeText(getApplicationContext(), message ,Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MessageDisplay.class);
//                Bundle b = new Bundle();
//                b.putParcelable("message", (Parcelable) message);
                i.putExtra("key" , message.body +"`"+ message.date +"`"+ message.address);
                startActivity(i);


            }
        });

    }

    private void classify(int MODE) {

        Set<Integer> keys = id_to_messages.keySet();

        if(MODE == RETRAIN) {
            try {
                classifier.retrain_the_model(id_to_messages, spam_messages_training, ham_messages_training);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messages_classified = classifier.classify_all(id_to_messages);


        id_list_non_spam.clear();
        id_list_spam.clear();
        for (int key : keys){
            if(messages_classified.get(key) == Message.SPAM) {
                id_list_spam.add(key);
            }
            else if(messages_classified.get(key) == Message.NOT_SPAM) {
                id_list_non_spam.add(key);
            }
        }

        Collections.sort(id_list_non_spam, Collections.<Integer>reverseOrder());
        Collections.sort(id_list_spam, Collections.<Integer>reverseOrder());

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
            }

        } catch (FileNotFoundException e) {
            Log.e("Error", "Never classified yet");
            e.printStackTrace();
        }
    }

    @Subscribe
    public void addMessageToList(Message message) {
        Log.e("broadcast", "Entered subscribed event");
        int id = message.id;
        if(!id_to_messages.containsKey(id)) {
            id_list.add(0, id);
            id_to_messages.put(id, message);
            if (classifier.classify(message.body, message.address) == Message.SPAM) {
                id_list_spam.add(0, id);
            } else {
                id_list_non_spam.add(0, id);
            }
            Log.e("broadcast", message.to_string_for_debug());
            Log.e("broadcast", id_to_messages.get(id_list_non_spam.get(0)).to_string_for_debug());
            fill_the_layout_with_messages();
        }
    }

    void readMessages() {
        String INBOX = "content://sms/inbox";

        Cursor cursor = getContentResolver().query(Uri.parse(INBOX), null, null, null, null);

        arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, id_list_non_spam, id_to_messages);
        listView.setAdapter(arrayAdapter);

        if (cursor.moveToFirst()) { // must check the result to prevent exception

            int BODY = cursor.getColumnIndex("body");
            int ID = cursor.getColumnIndex("_id");
            int PERSON = cursor.getColumnIndex("person");
            int ADDRESS = cursor.getColumnIndex("address");
            int DATE = cursor.getColumnIndex("date");

            do {

                String body = cursor.getString(BODY);
                int id = cursor.getInt(ID);
                String person = cursor.getString(PERSON);
                String date = Message.millisToTime(cursor.getLong(DATE));
                String address = cursor.getString(ADDRESS);

                id_list.add(id);

                if (id_to_messages.containsKey(id)) {
                    id_to_messages.get(id).set_message(body, person, address, date);
                } else {
                    id_to_messages.put(id, new Message(id, body, person, address, date));
                    id_to_messages.get(id).spam = classifier.classify(body, address);
                }
                switch (id_to_messages.get(id).spam){
                    case Message.NOT_SPAM: id_list_non_spam.add(id); break;
                    case Message.SPAM: id_list_spam.add(id); break;
                }

                arrayAdapter.notifyDataSetChanged();

            } while (cursor.moveToNext());
        } else {
            Log.e("Error", "no messages");
        }
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
                    ham_messages_training.put(id_from_pos, id_to_messages.get(id_from_pos).body);
                    if (spam_messages_training.containsKey(id_from_pos)) {
                        spam_messages_training.remove(id_from_pos);
                    }
                    if (messages_dataSet.containsKey(id_from_pos)) {
                        messages_dataSet.remove(id_from_pos);
                    }
                    classify(RETRAIN);
                }
                break;
            case HARDCODE_AS_SPAM:
                if (!spam_messages_training.containsKey(id_from_pos)) {
                    id_to_messages.get(id_from_pos).hard_coded = Message.YES;
                    id_to_messages.get(id_from_pos).spam = Message.SPAM;
                    spam_messages_training.put(id_from_pos, id_to_messages.get(id_from_pos).body);
                    if (ham_messages_training.containsKey(id_from_pos)) {
                        ham_messages_training.remove(id_from_pos);
                    }
                    if (messages_dataSet.containsKey(id_from_pos)) {
                        messages_dataSet.remove(id_from_pos);
                    }
                    classify(RETRAIN);
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
                    messages_dataSet.put(id_from_pos, id_to_messages.get(id_from_pos).body);
                }
                classify(RETRAIN);
                break;
            case BLACKLIST_CONTACT:
                classifier.addSenderToListAndRemoveFromOther(id_to_messages.get(id_from_pos).address,
                        "blacklisted", "whitelisted");
                classify(CLASSIFY_BY_ADDRESS_ONLY);
                break;
            case WHITELIST_CONTACT:
                classifier.addSenderToListAndRemoveFromOther(id_to_messages.get(id_from_pos).address,
                        "whitelisted", "blacklisted");
                Log.e("listing", "whitelisting : " +  id_to_messages.get(id_from_pos).address);
                classify(CLASSIFY_BY_ADDRESS_ONLY);
                break;
            case DELETE:
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
            String[] menuItems = {"Mark Spam", "Mark Non-Spam", "Unmark", "Delete", "Blacklist Contact", "Whitelist Contact"};
            menu.add(Menu.NONE, HARDCODE_AS_SPAM, 0, menuItems[0]);
            menu.add(Menu.NONE, HARDCODE_AS_HAM, 0, menuItems[1]);
            menu.add(Menu.NONE, UNMARK, 0, menuItems[2]);
            menu.add(Menu.NONE, DELETE, 0, menuItems[3]);
            menu.add(Menu.NONE, BLACKLIST_CONTACT, 0, menuItems[4]);
            menu.add(Menu.NONE, WHITELIST_CONTACT, 0, menuItems[5]);
        }
    }

    private void fill_the_layout_with_messages() {

        switch(SHOWING_SPAM_OR_HAM){
            case Message.NOT_SPAM:{
                arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, id_list_non_spam, id_to_messages);
                listView.setAdapter(arrayAdapter);
                Log.e("filling layout", "Displaying NOT SPAM");
            }
            break;
            case Message.SPAM:{
                arrayAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, id_list_spam, id_to_messages);
                listView.setAdapter(arrayAdapter);
                Log.e("filling layout", "Displaying SPAM");
            }
            break;
        }
    }




    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("Writing", "saving model started");
/*        try {
            classifier.nbc_classifier.saveClassifier();
        } catch (IOException e) {
            Log.e("Writing", "unable to save model");
            e.printStackTrace();
        }
*/
        Log.e("Writing", "writing to file started");
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
        /*try {
            classifier.nbc_classifier.saveClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Log.e("Error", "writing to file ends");

    }

    /**
     * Function to create and handle click events in Navigation drawer
     */
    private void handle_navigation_view() {

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                /**
                 * Navigation Drawer Elements
                 */
                switch (menuItem.getItemId()){
                    case R.id.TOOLBAR_LANGUAGES:{
                        Intent intent = new Intent(getApplicationContext(), Languages.class);
                        startActivity(intent);
                        return true;
                    }

                    case R.id.TOOLBAR_BLACKLIST:{
                        Intent intent = new Intent(getApplicationContext(), Blacklist.class);
                        startActivity(intent);
                        return true;
                    }

                    case R.id.TOOLBAR_WHITELIST:{
                        Intent intent = new Intent(getApplicationContext(), Whitelist.class);
                        startActivity(intent);
                        return true;
                    }

                    case R.id.TOOLBAR_NON_SPAM:{
                        SHOWING_SPAM_OR_HAM = Message.NOT_SPAM;
                        invalidateOptionsMenu();
                        fill_the_layout_with_messages();
                        getSupportActionBar().setTitle("Inbox");  // provide compatibility to all the versions
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#28539b")));
                        return true;
                    }

                    case R.id.TOOLBAR_SPAM:{
                        SHOWING_SPAM_OR_HAM = Message.SPAM;
                        invalidateOptionsMenu();
                        fill_the_layout_with_messages();
                        getSupportActionBar().setTitle("Spam");  // provide compatibility to all the versions
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff4242")));
                        return true;
                    }
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }


            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open, R.string.close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }


        };

        //Setting the actionbarToggle to drawer layout
        //drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);

    }

    /**
     * @param item list of navigation drawer menu items
     * @return toggle Drawer when clicked in the hamburger icon
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
