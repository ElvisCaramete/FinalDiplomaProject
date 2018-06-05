package com.project.diploma.elvis.diplomaproject.Blacklist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.diploma.elvis.diplomaproject.R;
import com.project.diploma.elvis.diplomaproject.Utils.DatabaseUtility_Blacklist;

import java.util.List;

/**
 * Created by Elvis on 1/1/2018.
 */

public class BlackListActivity extends AppCompatActivity implements View.OnClickListener {


    private Button add_to_blacklist;
    private Button contact_list;
    public ListView listview;
    private DatabaseUtility_Blacklist blacklist_utils;
    public static List<Blacklist> blacklist;

    public static final String BLACKLIST_PREF = "blacklist_pref";
    String[] blacklistNO = new String[10];




    public static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        add_to_blacklist = (Button) findViewById(R.id.add_blacklist_btn);
        contact_list = (Button) findViewById(R.id.contact_list);

        contact_list.setOnClickListener(this);
        add_to_blacklist.setOnClickListener(this);

        listview = (ListView) findViewById(R.id.listview);


        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        final View rowView = inflater.inflate(R.layout.black_list_item, listview, false);
        listview.addHeaderView(rowView);
    }

    @SuppressLint("SetTextI18n")
    private void noRecord() {
        if (blacklist.size() == 0) {
            final TextView noRecord = new TextView(this);
            noRecord.setTextSize(30);
            noRecord.setGravity(1);
            noRecord.setText("No Record Found !!");
            ((ImageButton) findViewById(R.id.delete)).setImageResource(R.drawable.sad_face);
            listview.addFooterView(noRecord);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == add_to_blacklist) {
            Intent myIntent = new Intent(BlackListActivity.this, AddToBlacklistActivity.class);
            BlackListActivity.this.startActivity(myIntent);
        }
        if (v == contact_list) {
            Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();

                String[] PROJECTION ={
                        ContactsContract.Contacts._ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                };
                assert contactUri != null;
                @SuppressLint("Recycle") Cursor cursor = getContentResolver()
                        .query(contactUri, PROJECTION, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int contactNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(contactNumber);

                int contactName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
                String name = cursor.getString(contactName);

                Toast.makeText(getApplicationContext(), number + name, Toast.LENGTH_LONG).show();
                final Blacklist phone = new Blacklist();

                phone.name = name;
                phone.phoneNumber = number;

                blacklist_utils.create(phone);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        blacklist_utils = new DatabaseUtility_Blacklist(this);
        blacklist = blacklist_utils.getBlacklistRecords();
        if (listview.getChildCount() > 1)
            listview.removeFooterView(listview.getChildAt(listview.getChildCount() - 1));
        listview.setAdapter(new CustomAdapter(this, R.layout.black_list_item, blacklist));
        noRecord();
    }

    public class CustomAdapter extends ArrayAdapter<String> {

        private LayoutInflater inflater;
        private List<Blacklist> records;

        CustomAdapter(Context context, int resource, @SuppressWarnings("rawtypes") List objects) {
            super(context, resource, objects);

            this.records = objects;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View view, @NonNull ViewGroup parent) {

            if (view == null)
                view = inflater.inflate(R.layout.black_list_item, parent, false);

            final Blacklist phoneNumber = records.get(position);

            ((TextView) view.findViewById(R.id.name)).setText(phoneNumber.name);
            ((TextView) view.findViewById(R.id.phone_number)).setText(phoneNumber.phoneNumber);

            if (position == 0) {
                ((ImageButton) findViewById(R.id.delete)).setImageResource(R.drawable.smile_face);
            }
            ImageButton deleteImageView = (ImageButton) view.findViewById(R.id.delete);
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        blacklist_utils.delete(blacklist.get(position));
                        blacklist.remove(position);
                        listview.invalidateViews();
                        noRecord();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            for(int i=0;i< blacklist.size();i++) {
                blacklistNO[i]=blacklist.get(i).getPhoneNumber();
            }
            SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(BLACKLIST_PREF,MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(blacklistNO);
            prefsEditor.putString("Blacklist", json);
            prefsEditor.apply();
            return view;
        }

    }

}
