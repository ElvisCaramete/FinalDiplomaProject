package com.project.diploma.elvis.diplomaproject.Utils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Elvis on 1/1/2018.
 */

public class DatabaseHelper_Blacklist extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "blacklist.db";

    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_BLACKLIST = "blacklist";

    private static final String TABLE_CREATE = "create table "  + TABLE_BLACKLIST + "( id "
            + " integer primary key autoincrement, name text not null, phone_number  text not null);";

    public DatabaseHelper_Blacklist(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

}