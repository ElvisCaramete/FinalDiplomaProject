package com.project.diploma.elvis.diplomaproject.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SyncCalendarEvents {


    public static boolean checkOngoingMeeting(Context context) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        assert cursor != null;
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        for (int i = 0; i < CNames.length; i++) {

            TimeZone tz = TimeZone.getDefault();
            long evStartFinal = Long.parseLong(cursor.getString(3)) - tz.getOffset(Long.parseLong(cursor.getString(3)));

            if (!getTime(evStartFinal).equals("00:00:00")) {
                //System.out.println(cursor.getString(1));
                if (System.currentTimeMillis() > Long.parseLong(cursor.getString(3)))
                    if (System.currentTimeMillis() < Long.parseLong(cursor.getString(4))) {
                        System.out.println("Current device time :" + getTime(System.currentTimeMillis()));
                        System.out.println("Current date of device: " + formattedDate);
                        System.out.println("Ongoing meeting :" + cursor.getString(1));
                        System.out.println("Start time of the event: " + getTime(Long.parseLong(cursor.getString(3))));
                        System.out.println("Start date of the event: " + getDate(Long.parseLong(cursor.getString(3))));
                        System.out.println("End time of the event :" + getTime(Long.parseLong(cursor.getString(4))));
                        System.out.println("End time of the event :" + getDate(Long.parseLong(cursor.getString(4))));
                        return true;
                    }
            }
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }
        return false;
    }

    public static ArrayList<String> returnCalendarEvent(Context context) {
        ArrayList<String> events = new ArrayList<>();
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        assert cursor != null;
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];
        events.clear();
        for (int i = 0; i < CNames.length; i++) {
            TimeZone tz = TimeZone.getDefault();
            long evStartFinal = Long.parseLong(cursor.getString(3)) - tz.getOffset(Long.parseLong(cursor.getString(3)));
            if (!getTime(evStartFinal).equals("00:00:00")) {
                if (System.currentTimeMillis() < Long.parseLong(cursor.getString(4))) {
                    System.out.println("Name: " + cursor.getString(1));
                    events.add("\nName of the event: " + cursor.getString(1) + "    \n >   Start time: " + getDateCalendarEvent(Long.parseLong(cursor.getString(3))) + "\n >   End time: " + getDateCalendarEvent(Long.parseLong(cursor.getString(4)))+"\n");
                }
            }
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();
        }

        return events;
    }

    private static String getTime(long miliSeconds) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(miliSeconds);
        return formatter.format(calendar.getTime());
    }

    private static String getDate(long miliSeconds) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(miliSeconds);
        return formatter.format(calendar.getTime());
    }

    private static String getDateCalendarEvent(long miliSeconds){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(miliSeconds);
        return formatter.format(calendar.getTime());
    }
}
