package com.project.diploma.elvis.diplomaproject.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.project.diploma.elvis.diplomaproject.Blacklist.Blacklist;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Elvis on 1/1/2018.
 */

public class DatabaseUtility_Blacklist {

    private SQLiteDatabase database;
    private DatabaseHelper_Blacklist dbHelper;

    public DatabaseUtility_Blacklist(Context context) {
        dbHelper = new DatabaseHelper_Blacklist(context);
        open();
    }

    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Blacklist create(final Blacklist blacklist) {

        final ContentValues values = new ContentValues();
        values.put("phone_number", blacklist.phoneNumber);
        values.put("name", blacklist.name);
        final long id = database.insert(DatabaseHelper_Blacklist.TABLE_BLACKLIST , null, values);
        blacklist.id = id;
        return blacklist;
    }

    public void delete(final Blacklist blacklist) {
        database.delete(DatabaseHelper_Blacklist.TABLE_BLACKLIST, "phone_number = '" + blacklist.phoneNumber + "'", null);
    }

    public List<Blacklist> getBlacklistRecords() {
        final List<Blacklist> blacklistRecord = new ArrayList<>();
        final Cursor cursor = database.query(DatabaseHelper_Blacklist.TABLE_BLACKLIST, new String[]{"id","name","phone_number"}, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            final Blacklist number = new Blacklist();

            number.id = cursor.getLong(0);
            number.name=cursor.getString(1);
            number.phoneNumber = cursor.getString(2);

            blacklistRecord.add(number);

            cursor.moveToNext();
        }
        return blacklistRecord;
    }
}
