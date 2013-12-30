package com.mikroskil.android.qattend.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String CONTENT_AUTHORITY = "com.mikroskil.android.qattend.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private Contract() {}

    public static class Event implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.qattend.events";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qattend.event";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("events").build();

        public static final String TABLE = "Event";
        public static final String COL_TITLE = "title";
        public static final String COL_START_DATE = "startDate";
        public static final String COL_END_DATE = "endDate";
        public static final String COL_LOCATION = "location";
        public static final String COL_CREATED_AT = "createdAt";
        public static final String COL_UPDATED_AT = "updatedAt";
    }

}
