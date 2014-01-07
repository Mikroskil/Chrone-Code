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
        db.execSQL(
            "CREATE TABLE " + Contract.Organization.TABLE + " (" +
                Contract.Organization._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Organization.COL_OBJ_ID + " TEXT NOT NULL UNIQUE," +
                Contract.Organization.COL_NAME + " TEXT NOT NULL," +
                Contract.Organization.COL_USERNAME + " TEXT NOT NULL UNIQUE," +
                Contract.Organization.COL_EMAIL + " TEXT," +
                Contract.Organization.COL_ABOUT + " TEXT," +
                Contract.Organization.COL_MEMBER_COUNT + " INTEGER DEFAULT 0," +
                Contract.Organization.COL_CREATED_AT + " TEXT DEFAULT (DATETIME('NOW'))," +
                Contract.Organization.COL_UPDATED_AT + " TEXT DEFAULT (DATETIME('NOW'))" +
            ")"
        );
        db.execSQL(
            "CREATE TABLE " + Contract.Event.TABLE + " (" +
                Contract.Event._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Event.COL_OBJ_ID + " TEXT UNIQUE," +
                Contract.Event.COL_TITLE + " TEXT NOT NULL," +
                Contract.Event.COL_START_DATE  + " TEXT NOT NULL," +
                Contract.Event.COL_END_DATE + " TEXT NOT NULL," +
                Contract.Event.COL_LOCATION + " TEXT NOT NULL," +
                Contract.Event.COL_PRIVACY + " INTEGER CHECK(" + Contract.Event.COL_PRIVACY + "=0 OR " + Contract.Event.COL_PRIVACY + "=1) DEFAULT 0," +
                Contract.Event.COL_DESC + " TEXT," +
                Contract.Event.COL_HOST_BY + " TEXT NOT NULL," +
                Contract.Event.COL_TICKET_COUNT + " INTEGER DEFAULT 0," +
                Contract.Event.COL_CREATED_AT + " TEXT DEFAULT (DATETIME('NOW'))," +
                Contract.Event.COL_UPDATED_AT + " TEXT DEFAULT (DATETIME('NOW'))" +
            ")"
        );
        db.execSQL(
            "CREATE TABLE " + Contract.Member.TABLE + " (" +
                Contract.Member._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Member.COL_OBJ_ID + " TEXT NOT NULL UNIQUE," +
                Contract.Member.COL_NAME + " TEXT NOT NULL," +
                Contract.Member.COL_USERNAME + " TEXT NOT NULL UNIQUE," +
                Contract.Member.COL_EMAIL + " TEXT NOT NULL," +
                Contract.Member.COL_GENDER + " INTEGER CHECK(" + Contract.Member.COL_GENDER + "=0 OR " + Contract.Member.COL_GENDER + "=1) NOT NULL," +
                Contract.Member.COL_PHONE + " TEXT," +
                Contract.Member.COL_ABOUT + " TEXT," +
                Contract.Member.COL_CREATED_AT + " TEXT DEFAULT (DATETIME('NOW'))," +
                Contract.Member.COL_UPDATED_AT + " TEXT DEFAULT (DATETIME('NOW'))" +
            ")"
        );
        db.execSQL(
            "CREATE TABLE " + Contract.Membership.TABLE + " (" +
                Contract.Membership._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Membership.COL_OBJ_ID + " TEXT UNIQUE," +
                Contract.Membership.COL_APPLICANT_FROM + " TEXT NOT NULL," +
                Contract.Membership.COL_APPLY_TO + " TEXT NOT NULL," +
                Contract.Membership.COL_APPROVED + " INTEGER CHECK(" + Contract.Membership.COL_APPROVED + "=0 OR " + Contract.Membership.COL_APPROVED + "=1) DEFAULT 0," +
                Contract.Membership.COL_CREATED_AT + " TEXT DEFAULT (DATETIME('NOW'))," +
                Contract.Membership.COL_UPDATED_AT + " TEXT DEFAULT (DATETIME('NOW'))" +
            ")"
        );
        db.execSQL(
            "CREATE TABLE " + Contract.Ticket.TABLE + " (" +
                    Contract.Ticket._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Contract.Ticket.COL_OBJ_ID + " TEXT UNIQUE," +
                    Contract.Ticket.COL_PARTICIPANT + " TEXT NOT NULL," +
                    Contract.Ticket.COL_PARTICIPATE_TO + " TEXT NOT NULL," +
                    Contract.Ticket.COL_VERIFIED + " INTEGER CHECK(" + Contract.Ticket.COL_VERIFIED + "=0 OR " + Contract.Ticket.COL_VERIFIED + "=1) DEFAULT 0," +
                    Contract.Ticket.COL_CREATED_AT + " TEXT DEFAULT (DATETIME('NOW'))," +
                    Contract.Ticket.COL_UPDATED_AT + " TEXT DEFAULT (DATETIME('NOW'))" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(QattendApp.TAG, String.format("Upgrading database from version %s to %s", oldVersion, newVersion));
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Ticket.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Membership.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Member.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Event.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Organization.TABLE);
        onCreate(db);
    }

}
