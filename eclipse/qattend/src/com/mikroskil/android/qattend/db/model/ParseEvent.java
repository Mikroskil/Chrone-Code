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

@ParseClassName("Event")
public class ParseEvent extends ParseObject {

    public ParseEvent() {}

    public String getTitle() {
        return getString(Contract.Event.COL_TITLE);
    }

    public void setTitle(String title) {
        put(Contract.Event.COL_TITLE, title);
    }

    public Date getStartDate() {
        return getDate(Contract.Event.COL_START_DATE);
    }

    public void setStartDate(Date startDate) {
        put(Contract.Event.COL_START_DATE, startDate);
    }

    public Date getEndDate() {
        return getDate(Contract.Event.COL_END_DATE);
    }

    public void setEndDate(Date endDate) {
        put(Contract.Event.COL_END_DATE, endDate);
    }

    public String getLocation() {
        return getString(Contract.Event.COL_LOCATION);
    }

    public void setLocation(String location) {
        put(Contract.Event.COL_LOCATION, location);
    }

    public String getDesc() {
        return getString(Contract.Event.COL_DESC);
    }

    public void setDesc(String desc) {
        put(Contract.Event.COL_DESC, desc);
    }

    public boolean getPrivacy() {
        return getBoolean(Contract.Event.COL_PRIVACY);
    }

    public void setPrivacy(boolean privacy) {
        put(Contract.Event.COL_PRIVACY, privacy);
    }

    public String getHostBy() {
        return getParseObject(Contract.Event.COL_HOST_BY).getObjectId();
    }

    public void setHostBy(ParseOrganization org) {
        put(Contract.Event.COL_HOST_BY, org);
    }

    public int getTicketCount() {
        return getInt(Contract.Event.COL_TICKET_COUNT);
    }

    public void initTicketCount() {
        put(Contract.Event.COL_TICKET_COUNT, 0);
    }

    public void incTicketCount() {
        increment(Contract.Event.COL_TICKET_COUNT);
    }

    public ContentValues getContentValues() {
        SimpleDateFormat formatter = new SimpleDateFormat(Contract.DATE_TIME_FORMAT);
        ContentValues values = new ContentValues();
        if (getObjectId() != null) values.put(Contract.Event.COL_OBJ_ID, getObjectId());
        values.put(Contract.Event.COL_TITLE, getTitle());
        values.put(Contract.Event.COL_START_DATE, formatter.format(getStartDate()));
        values.put(Contract.Event.COL_END_DATE, formatter.format(getEndDate()));
        values.put(Contract.Event.COL_LOCATION, getLocation());
        values.put(Contract.Event.COL_DESC, getDesc());
        values.put(Contract.Event.COL_PRIVACY, getPrivacy());
        values.put(Contract.Event.COL_HOST_BY, getHostBy());
        if (getCreatedAt() != null) values.put(Contract.Event.COL_CREATED_AT, formatter.format(getCreatedAt()));
        if (getUpdatedAt() != null) values.put(Contract.Event.COL_UPDATED_AT, formatter.format(getUpdatedAt()));
        return values;
    }

    public static Event fromCursor(Cursor cursor) {
        String objId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_OBJ_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_TITLE));
        String startDate = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_START_DATE));
        String endDate = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_END_DATE));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_LOCATION));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_DESC));
        String hostBy = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_HOST_BY));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_CREATED_AT));
        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event.COL_UPDATED_AT));
        int privacy = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Event.COL_PRIVACY));
        return new Event(objId, title, startDate, endDate, location, desc, privacy, hostBy, createdAt, updatedAt);
    }

    public static class Event
    {
        public final String objId;
        public final String title;
        public final Date startDate;
        public final Date endDate;
        public final String location;
        public final String desc;
        public final boolean privacy;
        public final String hostBy;
        public final Date createdAt;
        public final Date updatedAt;

        public Event(String objId, String title, String startDate, String endDate, String location,
                     String desc, int privacy, String hostBy, String createdAt, String updatedAt) {
            SimpleDateFormat parser = new SimpleDateFormat(Contract.DATE_TIME_FORMAT);
            this.objId = objId;
            this.title = title;
            this.location = location;
            this.desc = desc;
            this.privacy = privacy > 0;
            this.hostBy = hostBy;

            Date tmp1 = null;
            Date tmp2 = null;
            Date tmp3 = null;
            Date tmp4 = null;
            try {
                tmp1 = parser.parse(startDate);
                tmp2 = parser.parse(endDate);
                tmp3 = parser.parse(createdAt);
                tmp4 = parser.parse(updatedAt);
            } catch (ParseException e) {
                Log.e(QattendApp.TAG, "event parse date: " + e.getMessage());
            }
            this.startDate = tmp1;
            this.endDate = tmp2;
            this.createdAt = tmp3;
            this.updatedAt = tmp4;
        }
    }

}
