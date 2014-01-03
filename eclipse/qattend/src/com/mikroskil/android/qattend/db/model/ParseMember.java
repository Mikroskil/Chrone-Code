package com.mikroskil.android.qattend.db.model;

import android.content.ContentValues;

import com.mikroskil.android.qattend.db.Contract;
import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;

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

    public ContentValues getContentValues() {
        SimpleDateFormat formatter = new SimpleDateFormat(Contract.DATE_FORMAT);
        ContentValues values = new ContentValues();
        values.put(Contract.Member._ID, getObjectId());
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

}
