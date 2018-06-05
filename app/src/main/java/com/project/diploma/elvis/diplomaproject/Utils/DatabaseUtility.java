package com.project.diploma.elvis.diplomaproject.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.project.diploma.elvis.diplomaproject.Whitelist.Whitelist;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Elvis on 1/1/2018.
 */

public class DatabaseUtility {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DatabaseUtility(Context context) {
        dbHelper = new DatabaseHelper(context);
        open();
    }

    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Whitelist create(final Whitelist whiteList) {

        final ContentValues values = new ContentValues();
        values.put("phone_number", whiteList.phoneNumber);
        values.put("name", whiteList.name);
        final long id = database.insert(DatabaseHelper.TABLE_WHITELIST , null, values);
        whiteList.id = id;
        return whiteList;
    }

    public void delete(final Whitelist whitelist) {
        database.delete(DatabaseHelper.TABLE_WHITELIST, "phone_number = '" + whitelist.phoneNumber + "'", null);
    }

    public List<Whitelist> getWhitelistRecords() {
        final List<Whitelist> whitelistRecord = new ArrayList<>();
        final Cursor cursor = database.query(DatabaseHelper.TABLE_WHITELIST, new String[]{"id","name","phone_number"}, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            final Whitelist number = new Whitelist();

            number.id = cursor.getLong(0);
            number.name=cursor.getString(1);
            number.phoneNumber = cursor.getString(2);

            whitelistRecord.add(number);

            cursor.moveToNext();
        }
        return whitelistRecord;
    }
}
