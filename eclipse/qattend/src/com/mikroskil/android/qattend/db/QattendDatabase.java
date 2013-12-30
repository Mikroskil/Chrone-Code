package com.mikroskil.android.qattend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QattendDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "qattend.db";

    private static final String TYPE_TEXT = " TEXT";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_EVENT =
            "CREATE TABLE " + Contract.Event.TABLE + " (" +
                    Contract.Event._ID + TYPE_TEXT + " PRIMARY KEY" + COMMA_SEP +
                    Contract.Event.COL_TITLE + TYPE_TEXT + COMMA_SEP +
                    Contract.Event.COL_START_DATE + TYPE_TEXT + COMMA_SEP +
                    Contract.Event.COL_END_DATE + TYPE_TEXT + COMMA_SEP +
                    Contract.Event.COL_LOCATION + TYPE_TEXT + COMMA_SEP +
                    Contract.Event.COL_CREATED_AT + TYPE_TEXT + COMMA_SEP +
                    Contract.Event.COL_UPDATED_AT + TYPE_TEXT +
            ")";

    private static final String SQL_DELETE_EVENT =
            "DROP TABLE IF EXISTS " + Contract.Event.TABLE;

    public QattendDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_EVENT);
        onCreate(db);
    }

}
