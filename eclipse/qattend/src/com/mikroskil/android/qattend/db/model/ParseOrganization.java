package com.mikroskil.android.qattend.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.db.Contract;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ParseClassName("Organization")
public class ParseOrganization extends ParseObject {

    public ParseOrganization() {}

    public String getName() {
        return getString(Contract.Organization.COL_NAME);
    }

    public void setName(String name) {
        put(Contract.Organization.COL_NAME, name);
    }

    public String getUsername() {
        return getString(Contract.Organization.COL_USERNAME);
    }

    public void setUsername(String username) {
        put(Contract.Organization.COL_USERNAME, username);
    }

    public String getEmail() {
        return getString(Contract.Organization.COL_EMAIL);
    }

    public void setEmail(String email) {
        put(Contract.Organization.COL_EMAIL, email);
    }

    public String getAbout() {
        return getString(Contract.Organization.COL_ABOUT);
    }

    public void setAbout(String about) {
        put(Contract.Organization.COL_ABOUT, about);
    }

    public int getMemberCount() {
        return getInt(Contract.Organization.COL_MEMBER_COUNT);
    }

    public void incrementMemberCount() {
        increment(Contract.Organization.COL_MEMBER_COUNT);
    }

    public ContentValues getContentValues() {
        SimpleDateFormat formatter = new SimpleDateFormat(Contract.DATE_FORMAT);
        ContentValues values = new ContentValues();
        values.put(Contract.Organization._ID, getObjectId());
        values.put(Contract.Organization.COL_NAME, getName());
        values.put(Contract.Organization.COL_USERNAME, getUsername());
        values.put(Contract.Organization.COL_EMAIL, getEmail());
        values.put(Contract.Organization.COL_ABOUT, getAbout());
        values.put(Contract.Organization.COL_MEMBER_COUNT, getMemberCount());
        values.put(Contract.Organization.COL_CREATED_AT, formatter.format(getCreatedAt()));
        values.put(Contract.Organization.COL_UPDATED_AT, formatter.format(getUpdatedAt()));
        return values;
    }

    public static Organization fromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Organization._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Organization.COL_NAME));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Organization.COL_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Organization.COL_EMAIL));
        String about = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Organization.COL_ABOUT));
        int memberCount = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Organization.COL_MEMBER_COUNT));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_CREATED_AT));
        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_UPDATED_AT));
        return new Organization(id, name, username, email, about, memberCount, createdAt, updatedAt);
    }

    public static class Organization
    {
        public final String id;
        public final String name;
        public final String username;
        public final String email;
        public final String about;
        public final int memberCount;
        public final Date createdAt;
        public final Date updatedAt;

        public Organization(String id, String name, String username, String email, String about,
                            int memberCount, String createdAt, String updatedAt) {
            SimpleDateFormat parser = new SimpleDateFormat(Contract.DATE_FORMAT);
            this.id = id;
            this.name = name;
            this.username = username;
            this.email = email;
            this.about = about;
            this.memberCount = memberCount;

            Date tmp1 = null;
            Date tmp2 = null;
            try {
                tmp1 = parser.parse(createdAt);
                tmp2 = parser.parse(updatedAt);
            } catch (ParseException e) {
                Log.e(QattendApp.TAG, "organization parse date: " + e.getMessage());
            }
            this.createdAt = tmp1;
            this.updatedAt = tmp2;
        }
    }

}
