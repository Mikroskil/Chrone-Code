package com.mikroskil.android.qattend.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.mikroskil.android.qattend.QattendApp;

public class QattendProvider extends ContentProvider {

    public static final int ROUTE_ORGS = 1;
    public static final int ROUTE_ORGS_ID = 2;
    public static final int ROUTE_EVENTS = 3;
    public static final int ROUTE_EVENTS_ID = 4;
    public static final int ROUTE_MEMBERS = 5;
    public static final int ROUTE_MEMBERS_ID = 6;
    public static final int ROUTE_MEMBERSHIPS = 7;
    public static final int ROUTE_MEMBERSHIPS_ID = 8;
    public static final int ROUTE_TICKETS = 9;
    public static final int ROUTE_TICKETS_ID = 10;

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
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "memberships", ROUTE_MEMBERSHIPS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "memberships/*", ROUTE_MEMBERSHIPS_ID);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "tickets", ROUTE_TICKETS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "tickets/*", ROUTE_TICKETS_ID);
    }

    private QattendDatabase mDbHelper;

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
            case ROUTE_MEMBERSHIPS:
                return Contract.Membership.CONTENT_TYPE;
            case ROUTE_MEMBERSHIPS_ID:
                return Contract.Membership.CONTENT_ITEM_TYPE;
            case ROUTE_TICKETS:
                return Contract.Ticket.CONTENT_TYPE;
            case ROUTE_TICKETS_ID:
                return Contract.Ticket.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int uriMatch = sUriMatcher.match(uri);
        Cursor cursor;

        switch (uriMatch) {
            case ROUTE_ORGS_ID:
                cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s=?",
                        Contract.Organization.TABLE, Contract.Organization._ID),
                        new String[] { uri.getLastPathSegment() });
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
                cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s=?",
                        Contract.Member.TABLE, Contract.Member._ID),
                        new String[] { uri.getLastPathSegment() });
                break;
            case ROUTE_MEMBERS:
                cursor = db.rawQuery(String.format("SELECT B.%s, B.%s, B.%s FROM %s AS A INNER JOIN %s AS B ON A.%s = B.%s " +
                        selection + " AND A.%s = ? ORDER BY A.%s DESC",
                        Contract.Member._ID, Contract.Member.COL_NAME, Contract.Member.COL_USERNAME,
                        Contract.Membership.TABLE, Contract.Member.TABLE,
                        Contract.Membership.COL_APPLICANT_FROM, Contract.Member.COL_OBJ_ID, Contract.Membership.COL_APPROVED,
                        Contract.Membership.COL_APPLY_TO,
                        Contract.Membership.COL_CREATED_AT),
                        selectionArgs);
                break;
            case ROUTE_MEMBERSHIPS:
                throw new UnsupportedOperationException(uri.toString());
            case ROUTE_MEMBERSHIPS_ID:
                cursor = db.rawQuery(String.format("SELECT %s FROM %s WHERE %s = ?",
                        Contract.Membership.COL_APPLICANT_FROM, Contract.Membership.TABLE,
                        Contract.Membership.COL_OBJ_ID),
                        new String[] { uri.getLastPathSegment() });
                break;
            case ROUTE_TICKETS:
                if (projection != null)
                    cursor = db.query(Contract.Ticket.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                else {
                    cursor = db.rawQuery(String.format("SELECT T.%s, M.%s, M.%s FROM %s AS T INNER JOIN %s AS M ON T.%s = M.%s WHERE T.%s = ? ",
                            Contract.Ticket._ID, Contract.Member.COL_NAME, Contract.Member.COL_USERNAME,
                            Contract.Ticket.TABLE, Contract.Member.TABLE,
                            Contract.Ticket.COL_PARTICIPANT, Contract.Member.COL_OBJ_ID,
                            Contract.Ticket.COL_PARTICIPATE_TO) + selection +
                            "ORDER BY T." + Contract.Ticket.COL_CREATED_AT + " DESC",
                            selectionArgs);
                }
                break;
            case ROUTE_TICKETS_ID:
                throw new UnsupportedOperationException(uri.toString());
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Log.d(QattendApp.TAG, String.format("uri=%s count=%s", uri, cursor.getCount()));
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int uriMatch = sUriMatcher.match(uri);
        Uri result;
        long id;

        switch (uriMatch) {
            case ROUTE_EVENTS:
                id = db.insertOrThrow(Contract.Event.TABLE, null, values);
                result = Uri.parse(Contract.Event.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ORGS:
                id = db.insertOrThrow(Contract.Organization.TABLE, null, values);
                result = Uri.parse(Contract.Organization.CONTENT_URI + "/" + id);
                break;
            case ROUTE_MEMBERS:
                id = db.insertOrThrow(Contract.Member.TABLE, null, values);
                result = Uri.parse(Contract.Member.CONTENT_URI + "/" + id);
                break;
            case ROUTE_MEMBERSHIPS:
                id = db.insertOrThrow(Contract.Membership.TABLE, null, values);
                result = Uri.parse(Contract.Membership.CONTENT_URI + "/" + id);
                break;
            case ROUTE_TICKETS:
                id = db.insertOrThrow(Contract.Ticket.TABLE, null, values);
                result = Uri.parse(Contract.Ticket.CONTENT_URI + "/" + id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Log.d(QattendApp.TAG, String.format("id=%s uri=%s values=%s", id, uri, values));
        if (id != -1) getContext().getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int uriMatch = sUriMatcher.match(uri);
        int count;

        switch (uriMatch) {
            case ROUTE_EVENTS_ID:
                if (values != null) {
                    count = db.update(Contract.Event.TABLE, values, Contract.Event._ID + " = ?",
                            new String[] { uri.getLastPathSegment() });
                } else {
                    count = db.rawQuery(String.format("UPDATE %s SET %2$s = %s + 1 WHERE %3$s = ?",
                            Contract.Event.TABLE, Contract.Event.COL_TICKET_COUNT,
                            Contract.Event.COL_OBJ_ID),
                            new String[] { uri.getLastPathSegment() }).getCount();
                }
                break;
            case ROUTE_TICKETS:
                count = db.update(Contract.Ticket.TABLE, values, whereClause, whereArgs);
                break;
            case ROUTE_MEMBERSHIPS:
                count = db.update(Contract.Membership.TABLE, values, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Log.d(QattendApp.TAG, String.format("count=%s uri=%s values=%s", count, uri, values));
        getContext().getContentResolver().notifyChange(uri, null, false);
        return count;
    }

}
