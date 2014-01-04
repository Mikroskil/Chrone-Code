package com.mikroskil.android.qattend.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mikroskil.android.qattend.NavigationDrawerFragment;

public class QattendProvider extends ContentProvider {

    private QattendDatabase mDbHelper;

    /**
     * URI ID for route: /organizations
     */
    public static final int ROUTE_ORGS = 1;

    /**
     * URI ID for route: /organizations/{ID}
     */
    public static final int ROUTE_ORGS_ID = 2;

    /**
     * URI ID for route: /events
     */
    public static final int ROUTE_EVENTS = 3;

    /**
     * URI ID for route: /events/{ID}
     */
    public static final int ROUTE_EVENTS_ID = 4;

    /**
     * URI ID for route: /members
     */
    public static final int ROUTE_MEMBERS = 5;

    /**
     * URI ID for route: /members/{ID}
     */
    public static final int ROUTE_MEMBERS_ID = 6;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "organizations", ROUTE_ORGS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "organizations/*", ROUTE_ORGS_ID);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "events", ROUTE_EVENTS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "events/*", ROUTE_EVENTS_ID);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "members", ROUTE_MEMBERS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "members/*", ROUTE_MEMBERS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new QattendDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ORGS:
                return Contract.Organization.CONTENT_TYPE;
            case ROUTE_ORGS_ID:
                return Contract.Organization.CONTENT_ITEM_TYPE;
            case ROUTE_EVENTS:
                return Contract.Event.CONTENT_TYPE;
            case ROUTE_EVENTS_ID:
                return Contract.Event.CONTENT_ITEM_TYPE;
            case ROUTE_MEMBERS:
                return Contract.Member.CONTENT_TYPE;
            case ROUTE_MEMBERS_ID:
                return Contract.Member.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int uriMatch = sUriMatcher.match(uri);
        Cursor cursor = null;

        switch (uriMatch) {
            case ROUTE_ORGS_ID:
                // unimplemented
                break;
            case ROUTE_ORGS:
                cursor = db.query(Contract.Organization.TABLE, projection, null, null, null, null, sortOrder);
                break;
            case ROUTE_EVENTS_ID:
                cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s=?",
                        Contract.Event.TABLE, Contract.Event._ID),
                        new String[] { uri.getLastPathSegment() });
                break;
            case ROUTE_EVENTS:
                cursor = db.query(Contract.Event.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_MEMBERS_ID:
                // unimplemented
                break;
            case ROUTE_MEMBERS:
                cursor = db.rawQuery(String.format("SELECT A.%s, B.%s, B.%s FROM %s AS A INNER JOIN %s AS B ON A.%s = B.%s " + selection + " AND A.%s = ? ORDER BY A.%s DESC",
                        Contract.Membership._ID, Contract.Member.COL_NAME, Contract.Member.COL_USERNAME,
                        Contract.Membership.TABLE, Contract.Member.TABLE,
                        Contract.Membership.COL_APPLICANT_FROM, Contract.Member._ID, Contract.Membership.COL_APPROVED,
                        Contract.Membership.COL_APPLY_TO,
                        Contract.Membership.COL_CREATED_AT),
                        new String[] { NavigationDrawerFragment.getActiveOrgId() });
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
