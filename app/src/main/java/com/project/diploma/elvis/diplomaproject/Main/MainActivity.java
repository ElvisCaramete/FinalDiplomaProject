package com.project.diploma.elvis.diplomaproject.Main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.project.diploma.elvis.diplomaproject.Blacklist.BlackListActivity;
import com.project.diploma.elvis.diplomaproject.R;
import com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity;
import com.project.diploma.elvis.diplomaproject.Whitelist.WhiteListActivity;

import java.util.ArrayList;
import java.util.List;

import static com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity.SHARED_PREFS;
import static com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity.SWITCH1;
import static com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity.TEXTVIEW;


/**
 * Created by Elvis on 1/1/2018.
 */

public class MainActivity extends AppCompatActivity{

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    public static String[] permissions= new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.MODIFY_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button whiteList = (Button) findViewById(R.id.whitelist);
        Button settings = (Button) findViewById(R.id.settings);
        Button blacklist = (Button) findViewById(R.id.blacklist);

        whiteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, WhiteListActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, BlackListActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        checkPermissions();
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
        }
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                int cnt =0;
                if(grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length-1; i++){
                        if (grantResults[i] == 0) {
                            cnt++;
                            System.out.println(cnt);
                        }
                    }
                    if(cnt == grantResults.length-1){
                        System.out.println("All permissions are granted on Main Menu!");
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(SWITCH1, true);
                        editor.putString(TEXTVIEW, "Permissions granted!");
                        editor.apply();
                    }
                } else {
                    StringBuilder perStr = new StringBuilder();
                    for (String per : permissions) {
                        perStr.append("\n").append(per);
                    }
                }
                return;
            }
        }
    }
}
