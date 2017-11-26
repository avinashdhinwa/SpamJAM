package com.softwareengineering.spamjam;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Unknown User on 30-10-2017.
 */

public class MyAdapter extends ArrayAdapter {

    private static LayoutInflater inflater = null;
    HashMap<Integer, Message> id_to_messages;
    List<Integer> id_list;

    public MyAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List<Integer> id_list, HashMap<Integer, Message> id_to_messages) {
        super(activity, resource, id_list);
        this.id_list = id_list;
        this.id_to_messages = id_to_messages;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_main_messages_listview_row, null);
        }
//        if (position % 2 == 1) {
//            view.setBackgroundColor(Color.WHITE);
//        } else {
//            view.setBackgroundColor(Color.rgb(154	,255,	154));
//        }

        TextView sender = (TextView) view.findViewById(R.id.sender);
        TextView body_summary = (TextView) view.findViewById(R.id.body_summary);
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);
        ImageView image = (ImageView) view.findViewById(R.id.senders_image);

        if (position % 2 == 1) {
            image.setImageDrawable(view.getResources().getDrawable(R.drawable.man));
        } else {
            image.setImageDrawable(view.getResources().getDrawable(R.drawable.boss));

        }

        String sender_name = id_to_messages.get(id_list.get(position)).address;
        if(id_to_messages.get(id_list.get(position)).person != null){
            sender_name = id_to_messages.get(id_list.get(position)).person;
        }

        sender.setText(sender_name);
        body_summary.setText(id_to_messages.get(id_list.get(position)).body);
        timestamp.setText(id_to_messages.get(id_list.get(position)).date);

        return view;
    }
}
