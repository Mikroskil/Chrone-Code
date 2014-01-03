package com.mikroskil.android.qattend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mikroskil.android.qattend.QattendApp;

public class QattendDatabase extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Qattend.db";

    public QattendDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TYPE_TEXT = " TEXT";
        String TYPE_INTEGER = " INTEGER";
        String COMMA_SEP = ", ";

        db.execSQL(
            "CREATE TABLE " + Contract.Event.TABLE + " (" +
                Contract.Event._ID + TYPE_TEXT + " PRIMARY KEY" + COMMA_SEP +
                Contract.Event.COL_TITLE + TYPE_TEXT + COMMA_SEP +
                Contract.Event.COL_START_DATE + TYPE_TEXT + COMMA_SEP +
                Contract.Event.COL_END_DATE + TYPE_TEXT + COMMA_SEP +
                Contract.Event.COL_DESC + TYPE_TEXT + COMMA_SEP +
                Contract.Event.COL_PRIVACY + TYPE_INTEGER + COMMA_SEP +
                Contract.Event.COL_LOCATION + TYPE_TEXT + COMMA_SEP +
                Contract.Event.COL_HOST_BY + TYPE_TEXT + COMMA_SEP +
                Contract.Event.COL_CREATED_AT + TYPE_TEXT + COMMA_SEP +
                Contract.Event.COL_UPDATED_AT + TYPE_TEXT +
            ")"
        );
        db.execSQL(
            "CREATE TABLE " + Contract.Organization.TABLE + " (" +
                Contract.Organization._ID + TYPE_TEXT + " PRIMARY KEY" + COMMA_SEP +
                Contract.Organization.COL_NAME + TYPE_TEXT + COMMA_SEP +
                Contract.Organization.COL_USERNAME + TYPE_TEXT + COMMA_SEP +
                Contract.Organization.COL_EMAIL + TYPE_TEXT + COMMA_SEP +
                Contract.Organization.COL_ABOUT + TYPE_TEXT + COMMA_SEP +
                Contract.Organization.COL_MEMBER_COUNT + TYPE_INTEGER + COMMA_SEP +
                Contract.Organization.COL_CREATED_AT + TYPE_TEXT + COMMA_SEP +
                Contract.Organization.COL_UPDATED_AT + TYPE_TEXT +
            ")"
        );
        db.execSQL(
            "CREATE TABLE " + Contract.Membership.TABLE + " (" +
                Contract.Membership._ID + TYPE_TEXT + " PRIMARY KEY" + COMMA_SEP +
                Contract.Membership.COL_APPLICANT_FROM + TYPE_TEXT + COMMA_SEP +
                Contract.Membership.COL_APPLY_TO + TYPE_TEXT + COMMA_SEP +
                Contract.Membership.COL_APPROVED + TYPE_INTEGER + COMMA_SEP +
                Contract.Membership.COL_CREATED_AT + TYPE_TEXT + COMMA_SEP +
                Contract.Membership.COL_UPDATED_AT + TYPE_TEXT +
            ")"
        );
        db.execSQL(
            "CREATE TABLE " + Contract.Member.TABLE + " (" +
                Contract.Member._ID + TYPE_TEXT + " PRIMARY KEY" + COMMA_SEP +
                Contract.Member.COL_NAME + TYPE_TEXT + COMMA_SEP +
                Contract.Member.COL_USERNAME + TYPE_TEXT + COMMA_SEP +
                Contract.Member.COL_EMAIL + TYPE_TEXT + COMMA_SEP +
                Contract.Member.COL_GENDER + TYPE_INTEGER + COMMA_SEP +
                Contract.Member.COL_PHONE + TYPE_TEXT + COMMA_SEP +
                Contract.Member.COL_ABOUT + TYPE_TEXT + COMMA_SEP +
                Contract.Member.COL_CREATED_AT + TYPE_TEXT + COMMA_SEP +
                Contract.Member.COL_UPDATED_AT + TYPE_TEXT +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(QattendApp.TAG, String.format("Upgrading database from version %s to %s", oldVersion, newVersion));
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Event.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Organization.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Membership.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Member.TABLE);
        onCreate(db);
    }

}
