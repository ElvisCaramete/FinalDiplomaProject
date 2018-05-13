package com.project.diploma.elvis.diplomaproject.Settings;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.project.diploma.elvis.diplomaproject.R;

import static com.project.diploma.elvis.diplomaproject.Main.MainActivity.LOGIN;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String SWITCH1 = "switch1";
    public static final String TEXTVIEW = "textView";
    private static final String USERNAME_TAG = "username_tag";
    private static final String FLAG = "flag";
    private boolean switchOnOff;
    private Integer flag =0;
    private String permission_text;
    private String getUsername;
    private Button logIn;

    private Switch switch1;
    private TextView permissionText;
    private TextView userName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switch1 = (Switch) findViewById(R.id.switch1);
        permissionText = (TextView) findViewById(R.id.textView);
        userName = (TextView) findViewById(R.id.user);
        logIn = (Button) findViewById(R.id.logIn);

        switch1.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkPhoneState();
                if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                        Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    saveData();
                } else {
                    switch1.setChecked(false);
                }
            }
        });

        CheckPermissions();
        loadData();
        updateView();

        if(!userName.getText().equals("User undefined!")) {
            logIn.setText("Log Out");
        }else{
            logIn.setVisibility(View.INVISIBLE);
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                deleteAllAppAccounts();
            }
        });
    }

    public void deleteAllAppAccounts() {
        try {
            AccountManager am = AccountManager.get(getApplicationContext());
            Account[] accounts = am.getAccountsByType("com.google");
            for (Account a : accounts) {
                System.out.println("Account remmoved ? "+a.name);
                //am.removeAccount(a,null, null);
            }
            SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(LOGIN, true);
            editor.putString(USERNAME_TAG,null);
            editor.apply();
            logIn.setVisibility(View.INVISIBLE);
            userName.setText("User undefined!");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void saveData(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SWITCH1, switch1.isChecked());
        editor.putInt(FLAG,1);
        editor.putString(TEXTVIEW, permissionText.getText().toString());
        editor.apply();
        Toast.makeText(this, "DataSaved!", Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchOnOff = pref.getBoolean(SWITCH1,false);
        permission_text = pref.getString(TEXTVIEW,"Permissions not granted!");
        getUsername = pref.getString(USERNAME_TAG,"User undefined!");

    }

    public void updateView(){
        switch1.setChecked(switchOnOff);
        permissionText.setText(permission_text);
        if(!getUsername.equals("User undefined!") && getUsername.length()>10)
            userName.setText(getUsername.substring(0,getUsername.length()-10).toUpperCase());
        else
            userName.setText("User undefined!");
    }

    private void checkPhoneState(){
        if(ContextCompat.checkSelfPermission(SettingsActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                    Manifest.permission.READ_PHONE_STATE)){
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            } else{
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }else{
            permissionText.setText("Permissions granted!");
        }
    }

    private void CheckPermissions() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        flag = pref.getInt(FLAG, 0);
        if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                    Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                permissionText.setText("Permissions granted!");
                if (flag == 0) {
                    switch1.setChecked(true);
                }
            }
            if(flag == 0){
                switch1.setChecked(true);
            }

        }

    }
    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case 1:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(SettingsActivity.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){

                        switch1.setChecked(true);
                        permissionText.setText("Permissions granted!");
                    }

                }else {
                    permissionText.setText("Permissions not granted!");
                }
                return;
            }
        }
    }

}
