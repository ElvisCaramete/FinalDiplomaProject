package com.project.diploma.elvis.diplomaproject.Settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.project.diploma.elvis.diplomaproject.Main.MainActivity;
import com.project.diploma.elvis.diplomaproject.R;
import com.project.diploma.elvis.diplomaproject.Utils.SyncCalendarEvents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.diploma.elvis.diplomaproject.Main.MainActivity.MULTIPLE_PERMISSIONS;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String SWITCH1 = "switch1";
    public static final String TEXTVIEW = "textView";
    private boolean switchOnOff;
    private String permission_text;
    private Switch switch1;
    private TextView permissionText;
    private TextView calendarEvents;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        calendarEvents = (TextView) findViewById(R.id.calendarEvents);
        switch1 = (Switch) findViewById(R.id.switch1);
        permissionText = (TextView) findViewById(R.id.textView);


        loadData();
        updateView();


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("CheckPermissions: " + checkPermissions());
                if (!checkPermissions()) {
                    switch1.setChecked(false);
                }
                saveData();
            }
        });

        calendarEvents.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        updateView();
    }

    public void saveData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SWITCH1, switch1.isChecked());
        editor.putString(TEXTVIEW, permissionText.getText().toString());
        editor.apply();
        Toast.makeText(this, "DataSaved!", Toast.LENGTH_SHORT).show();
        System.out.println("Save Date: " + switch1.isChecked() + " " + permissionText.getText().toString());
    }

    public void loadData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchOnOff = pref.getBoolean(SWITCH1, false);
        permission_text = pref.getString(TEXTVIEW, "Permissions not granted!");
        System.out.println("Load Data: " + switchOnOff + " " + permission_text);

    }

    public void updateView() {
        switch1.setChecked(switchOnOff);
        permissionText.setText(permission_text);
        if (checkCalendarPermission()) {
            String finEvent = "";
            for (String event : SyncCalendarEvents.returnCalendarEvent(getApplicationContext())) {
                finEvent += event;
            }
            calendarEvents.setText(finEvent);
            calendarEvents.setTextSize(16);
        } else
            calendarEvents.setText("Calendar permissions not granted!");
        System.out.println("Update View: " + switchOnOff + " " + permission_text);
    }

    private boolean checkCalendarPermission() {
        int result = ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : MainActivity.permissions) {
            result = ContextCompat.checkSelfPermission(SettingsActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.get(0).equals("android.permission.MODIFY_PHONE_STATE")) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                int cnt = 0;
                if (grantResults.length > 0) {

                    for (int i = 0; i < grantResults.length - 1; i++) {
                        if (grantResults[i] == 0) {
                            cnt++;
                            System.out.println(cnt);
                        }
                    }
                    if (cnt == grantResults.length - 1) {
                        System.out.println("onRequestPermissionsResult: set switch true and text granted\n");
                        switch1.setChecked(true);
                        System.out.println("Switch: "+switch1.isChecked());
                        permissionText.setText(R.string.permissionGranted);
                        System.out.println("Text: "+permissionText.getText());

                        if (checkCalendarPermission())
                            calendarEvents.setText(Arrays.toString(SyncCalendarEvents.returnCalendarEvent(getApplicationContext()).toArray()));
                        else
                            calendarEvents.setText("Calendar permissions not granted!");
                        saveData();
                    }

                } else {
                    StringBuilder perStr = new StringBuilder();
                    for (String per : permissions) {
                        perStr.append("\n").append(per);
                    }

                    System.out.println("Permissions not granted: " + perStr);
                    // permissions list of don't granted permission
                }
            }
        }
    }

}
