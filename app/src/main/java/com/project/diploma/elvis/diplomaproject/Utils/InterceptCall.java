package com.project.diploma.elvis.diplomaproject.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.diploma.elvis.diplomaproject.Whitelist.Whitelist;

import java.lang.reflect.Method;

import static android.content.Context.MODE_PRIVATE;
import static com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity.SHARED_PREFS;
import static com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity.SWITCH1;
import static com.project.diploma.elvis.diplomaproject.Whitelist.WhiteListActivity.WHITELIST_PREF;

/**
 * Created by Elvis on 1/1/2018.
 */

public class InterceptCall extends BroadcastReceiver {

    private TelephonyManager mTelephonyManager;
    public static boolean isListening = false;
    private boolean switchOnOff;

    private boolean checkNumberInList(String[] list , String string){
        for(String s : list)
            if (string.equals(s))
                return true;
        return false;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {

        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        mTelephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences mPrefs = context.getSharedPreferences(WHITELIST_PREF,MODE_PRIVATE);
                Gson gson = new Gson();
                String json = mPrefs.getString("WhiteList", "");

                String[] listOfNumbers = gson.fromJson(json, String[].class);

                switchOnOff = prefs.getBoolean(SWITCH1, false);
                System.out.println(switchOnOff);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (switchOnOff && !checkNumberInList(listOfNumbers,new Whitelist(incomingNumber).getPhoneNumber()))
                        {
                            try {
                                Class c = Class.forName(mTelephonyManager.getClass().getName());
                                Method m = c.getDeclaredMethod("getITelephony");
                                m.setAccessible(true);
                                Object telephonyService = m.invoke(mTelephonyManager);
                                c = Class.forName(telephonyService.getClass().getName()); // Get its class
                                m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
                                m.setAccessible(true); // Make it accessible
                                m.invoke(telephonyService);
                                Toast.makeText(context, "Number blocked", Toast.LENGTH_SHORT).show();

                                // Add notification and logchat

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        }else {
                            Toast.makeText(context, "Number found in Whitelist", Toast.LENGTH_SHORT).show();
                            // Add notification and logchat
                        }
                }
            }

        }, PhoneStateListener.LISTEN_CALL_STATE);

    }

}