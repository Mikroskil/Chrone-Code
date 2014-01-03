package com.mikroskil.android.qattend.db.model;

import android.content.ContentValues;

import com.mikroskil.android.qattend.db.Contract;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;

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

}
