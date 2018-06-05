package com.project.diploma.elvis.diplomaproject.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.diploma.elvis.diplomaproject.Main.MainActivity;

import java.lang.reflect.Method;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;
import static com.project.diploma.elvis.diplomaproject.Blacklist.BlackListActivity.BLACKLIST_PREF;
import static com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity.SHARED_PREFS;
import static com.project.diploma.elvis.diplomaproject.Settings.SettingsActivity.SWITCH1;
import static com.project.diploma.elvis.diplomaproject.Whitelist.WhiteListActivity.WHITELIST_PREF;

/**
 * Created by Elvis on 1/1/2018.
 */

public class InterceptCall extends BroadcastReceiver {

    private TelephonyManager mTelephonyManager;
    private boolean switchOnOff;
    private NotificationManager notifManager;

    private boolean checkNumberInList(String[] list, String string) {
        if (list == null) {
            System.out.println("List null");
            return false;
        }
        for (String s : list) {
            if (s != null)
                if (string.contains(s.replaceAll("\\s+",""))) {
                    return true;
                }
        }
        return false;
    }

    public void onReceive(final Context context, final Intent intent) {

        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        mTelephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences whitePref = context.getSharedPreferences(WHITELIST_PREF, MODE_PRIVATE);
                SharedPreferences blackPref = context.getSharedPreferences(BLACKLIST_PREF, MODE_PRIVATE);
                Gson gson = new Gson();
                String json1 = whitePref.getString("WhiteList", "");
                String[] listOfNumbersWhite = gson.fromJson(json1, String[].class);
                String json2 = blackPref.getString("Blacklist", "");
                String[] listOfNumbersBlack = gson.fromJson(json2, String[].class);
                System.out.println("Numbers from blacklist: " + Arrays.toString(listOfNumbersBlack));
                System.out.println("Numbers from whitelist: " + Arrays.toString(listOfNumbersWhite));
                switchOnOff = prefs.getBoolean(SWITCH1, true);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (switchOnOff && checkNumberInList(listOfNumbersBlack, incomingNumber)) {
                            System.out.println("Number found in blacklist");
                            blockCall(context, incomingNumber);
                            break;
                        } else if (switchOnOff && !checkNumberInList(listOfNumbersWhite, incomingNumber) && SyncCalendarEvents.checkOngoingMeeting(context)) {
                            System.out.println("Number not found in whitelist and it is an ongoing meeting");
                            blockCall(context, incomingNumber);
                            break;

                        } else if (!switchOnOff) {
                            Toast.makeText(context, "No permission to block the call due to user settings", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Number found in Whitelist", Toast.LENGTH_SHORT).show();
                        }
                }
            }

        }, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void blockCall(Context context, String incomingNumber) {
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
            createNotification(context, contactExists(context, incomingNumber) + "\n" + incomingNumber);
            //TODO: Save to shared pref to put in log tab


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNotification(Context context, String aMessage) {
        final int NOTIFY_ID = 1002;

        String name = "my_package_channel";
        String id = "my_package_channel_1";
        String description = "my_package_first_channel";

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            assert notifManager != null;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);

            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            builder.setContentTitle("Blocked Call")  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(aMessage)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(context);

            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            builder.setContentTitle("Blocked Call")                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(aMessage)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }


    public String contactExists(Context context, String number) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                System.out.println("Number exists: true // " + cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                return cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        System.out.println("Number exists: false");
        return "Unknown";
    }


}