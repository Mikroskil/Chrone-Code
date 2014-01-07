package com.mikroskil.android.qattend.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.db.Contract;
import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ParseClassName("_User")
public class ParseMember extends ParseUser {

    public ParseMember() {}

    public String getName() {
        return getString(Contract.Member.COL_NAME);
    }

    public void setName(String name) {
        put(Contract.Member.COL_NAME, name);
    }

    public boolean getGender() {
        return getBoolean(Contract.Member.COL_GENDER);
    }

    public void setGender(boolean gender) {
        put(Contract.Member.COL_GENDER, gender);
    }

    public String getPhone() {
        return getString(Contract.Member.COL_PHONE);
    }

    public void setPhone(String phone) {
        put(Contract.Member.COL_PHONE, phone);
    }

    public String getAbout() {
        return getString(Contract.Member.COL_ABOUT);
    }

    public void setAbout(String about) {
        put(Contract.Member.COL_ABOUT, about);
    }

    public void initializeOrgCount() {
        put(Contract.Member.COL_ORG_COUNT, 0);
    }

    public void incOrgCount() {
        increment(Contract.Member.COL_ORG_COUNT);
    }

    public Date getLastSignIn() {
        return getDate(Contract.Member.COL_LAST_SIGN_IN);
    }

    public void setLastSignIn(Date lastSignIn) {
        put(Contract.Member.COL_LAST_SIGN_IN, lastSignIn);
    }

    public ContentValues getContentValues() {
        SimpleDateFormat formatter = new SimpleDateFormat(Contract.DATE_TIME_FORMAT);
        ContentValues values = new ContentValues();
        values.put(Contract.Member.COL_OBJ_ID, getObjectId());
        values.put(Contract.Member.COL_NAME, getName());
        values.put(Contract.Member.COL_USERNAME, getUsername());
        values.put(Contract.Member.COL_EMAIL, getEmail());
        values.put(Contract.Member.COL_GENDER, getGender());
        values.put(Contract.Member.COL_PHONE, getPhone());
        values.put(Contract.Member.COL_ABOUT, getAbout());
        values.put(Contract.Member.COL_CREATED_AT, formatter.format(getCreatedAt()));
        values.put(Contract.Member.COL_UPDATED_AT, formatter.format(getUpdatedAt()));
        return values;
    }

    public static Member fromCursor(Cursor cursor) {
        String objId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_OBJ_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_NAME));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_EMAIL));
        int gender = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Member.COL_GENDER));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_PHONE));
        String about = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_ABOUT));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_CREATED_AT));
        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Member.COL_UPDATED_AT));
        return new Member(objId, name, username, email, gender, phone, about, createdAt, updatedAt);
    }

    public static class Member
    {
        public final String objId;
        public final String name;
        public final String username;
        public final String email;
        public final boolean gender;
        public final String phone;
        public final String about;
        public final Date createdAt;
        public final Date updatedAt;

        public Member(String objId, String name, String username, String email, int gender, String phone,
                      String about, String createdAt, String updatedAt) {
            SimpleDateFormat parser = new SimpleDateFormat(Contract.DATE_TIME_FORMAT);
            this.objId = objId;
            this.name = name;
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.about = about;
            this.gender = gender > 0;

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
