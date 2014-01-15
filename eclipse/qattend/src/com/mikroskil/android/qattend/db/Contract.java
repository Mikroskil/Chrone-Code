package com.mikroskil.android.qattend.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;

public final class Contract {

    public static final String CONTENT_AUTHORITY = "com.mikroskil.android.qattend.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Contract() {}

    public static class Event implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.qattend.events";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qattend.event";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("events").build();

        public static final String TABLE = "Event";
        public static final String COL_OBJ_ID = "objectId";
        public static final String COL_TITLE = "title";
        public static final String COL_START_DATE = "startDate";
        public static final String COL_END_DATE = "endDate";
        public static final String COL_LOCATION = "location";
        public static final String COL_DESC = "description";
        public static final String COL_PRIVACY = "privacy";
        public static final String COL_HOST_BY = "hostBy";
        public static final String COL_TICKET_COUNT = "ticketCount";
        public static final String COL_CREATED_AT = "createdAt";
        public static final String COL_UPDATED_AT = "updatedAt";
    }

    public static class Organization implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.qattend.organizations";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qattend.organization";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("organizations").build();

        public static final String TABLE = "Organization";
        public static final String COL_OBJ_ID = "objectId";
        public static final String COL_NAME = "name";
        public static final String COL_USERNAME = "username";
        public static final String COL_EMAIL = "email";
        public static final String COL_ABOUT = "about";
        public static final String COL_MEMBER_COUNT = "memberCount";
        public static final String COL_OWN_BY = "ownBy";
        public static final String COL_CREATED_AT = "createdAt";
        public static final String COL_UPDATED_AT = "updatedAt";
    }

    public static class Membership implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.qattend.memberships";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qattend.membership";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("memberships").build();

        public static final String TABLE = "Membership";
        public static final String COL_OBJ_ID = "objectId";
        public static final String COL_APPLICANT_FROM = "applicantFrom";
        public static final String COL_APPLY_TO = "applyTo";
        public static final String COL_APPROVED = "approved";
        public static final String COL_CREATED_AT = "createdAt";
        public static final String COL_UPDATED_AT = "updatedAt";
    }

    public static class Member implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.qattend.members";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qattend.member";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("members").build();

        public static final String TABLE = "Member";
        public static final String COL_OBJ_ID = "objectId";
        public static final String COL_NAME = "name";
        public static final String COL_USERNAME = "username";
        public static final String COL_EMAIL = "email";
        public static final String COL_ABOUT = "about";
        public static final String COL_PHONE = "phone";
        public static final String COL_GENDER = "gender";
        public static final String COL_CREATED_AT = "createdAt";
        public static final String COL_UPDATED_AT = "updatedAt";
        public static final String COL_LAST_SIGN_IN = "lastSignIn";
        public static final String COL_ORG_COUNT = "orgCount";
    }

    public static class Ticket implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.qattend.tickets";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qattend.ticket";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("tickets").build();

        public static final String TABLE = "Ticket";
        public static final String COL_OBJ_ID = "objectId";
        public static final String COL_PARTICIPANT = "participant";
        public static final String COL_PARTICIPATE_TO = "participateTo";
        public static final String COL_VERIFIED = "verified";
        public static final String COL_CREATED_AT = "createdAt";
        public static final String COL_UPDATED_AT = "updatedAt";
    }
}
