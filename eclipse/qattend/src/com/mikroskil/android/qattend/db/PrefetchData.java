package com.mikroskil.android.qattend.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.db.model.ParseEvent;
import com.mikroskil.android.qattend.db.model.ParseMember;
import com.mikroskil.android.qattend.db.model.ParseMembership;
import com.mikroskil.android.qattend.db.model.ParseOrganization;
import com.mikroskil.android.qattend.db.model.ParseTicket;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrefetchData extends AsyncTask<Void, Void, Void> {

    private static final String LAST_SYNC = "last_sync";
    private static Date mLastSync;

    private Context mContext;
    private boolean mSyncFailed;

    public PrefetchData(Context context) {
        mContext = context;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String lastSync = sp.getString(LAST_SYNC, Contract.DATE_TIME_FORMATTER.format(new Date(0)));
        try {
            mLastSync = Contract.DATE_TIME_FORMATTER.parse(lastSync);
        } catch (java.text.ParseException e) {
            Log.e(QattendApp.TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(QattendApp.TAG, "prefetch data");

        ParseQuery<ParseOrganization> orgQ = ParseQuery.getQuery(ParseOrganization.class);
        orgQ.whereGreaterThan(Contract.Organization.COL_UPDATED_AT, mLastSync);
        orgQ.whereEqualTo(Contract.Organization.COL_OWN_BY, ParseMember.getCurrentUser());
        orgQ.findInBackground(new FindCallback<ParseOrganization>() {
            @Override
            public void done(List<ParseOrganization> orgs, ParseException e) {
                if (e == null) {
                    for (ParseOrganization org : orgs)
                        mContext.getContentResolver().insert(Contract.Organization.CONTENT_URI, org.getContentValues());

                    // query local org ids
                    Cursor c = mContext.getContentResolver().query(Contract.Organization.CONTENT_URI,
                            new String[]{Contract.Organization.COL_OBJ_ID}, null, null, null);
                    List<ParseOrganization> orgObjs = new ArrayList<ParseOrganization>();
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
                        orgObjs.add(ParseObject.createWithoutData(ParseOrganization.class,
                                c.getString(c.getColumnIndexOrThrow(Contract.Organization.COL_OBJ_ID))));
                    c.close();

                    queryMembership(orgObjs);
                } else {
                    mSyncFailed = true;
                    e.printStackTrace();
                }
            }
        });

        return null;
    }

    private void queryMembership(final List<ParseOrganization> orgObjs) {
        ParseQuery<ParseMembership> membershipQ = ParseQuery.getQuery(ParseMembership.class);
        membershipQ.whereGreaterThan(Contract.Membership.COL_UPDATED_AT, mLastSync);
        membershipQ.whereContainedIn(Contract.Membership.COL_APPLY_TO, orgObjs);
        membershipQ.include(Contract.Membership.COL_APPLICANT_FROM);
        membershipQ.findInBackground(new FindCallback<ParseMembership>() {
            @Override
            public void done(List<ParseMembership> memberships, ParseException e) {
                if (e == null) {
                    for (ParseMembership membership : memberships) {
                        mContext.getContentResolver().insert(Contract.Membership.CONTENT_URI, membership.getContentValues());
                        ParseMember user = (ParseMember) membership.getParseUser(Contract.Membership.COL_APPLICANT_FROM);
                        mContext.getContentResolver().insert(Contract.Member.CONTENT_URI, user.getContentValues());
                    }
                    queryEvent(orgObjs);
                } else {
                    mSyncFailed = true;
                    e.printStackTrace();
                }
            }
        });
    }

    private void queryEvent(final List<ParseOrganization> orgObjs) {
        ParseQuery<ParseEvent> eventQ = ParseQuery.getQuery(ParseEvent.class);
        eventQ.whereGreaterThan(Contract.Event.COL_UPDATED_AT, mLastSync);
        eventQ.whereContainedIn(Contract.Event.COL_HOST_BY, orgObjs);
        eventQ.findInBackground(new FindCallback<ParseEvent>() {
            @Override
            public void done(List<ParseEvent> events, ParseException e) {
                if (e == null) {
                    for (ParseEvent event : events)
                        mContext.getContentResolver().insert(Contract.Event.CONTENT_URI, event.getContentValues());

                    // query local event ids
                    Cursor c = mContext.getContentResolver().query(Contract.Event.CONTENT_URI,
                            new String[] { Contract.Event.COL_OBJ_ID }, null, null, null);
                    List<ParseEvent> eventObjs = new ArrayList<ParseEvent>();
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
                        eventObjs.add(ParseObject.createWithoutData(ParseEvent.class,
                                c.getString(c.getColumnIndexOrThrow(Contract.Event.COL_OBJ_ID))));
                    c.close();

                    queryTicket(eventObjs);
                } else {
                    mSyncFailed = true;
                    e.printStackTrace();
                }
            }
        });
    }

    private void queryTicket(final List<ParseEvent> eventObjs) {
        ParseQuery<ParseTicket> ticketQ = ParseQuery.getQuery(ParseTicket.class);
        ticketQ.whereGreaterThan(Contract.Ticket.COL_UPDATED_AT, mLastSync);
        ticketQ.whereContainedIn(Contract.Ticket.COL_PARTICIPATE_TO, eventObjs);
        ticketQ.findInBackground(new FindCallback<ParseTicket>() {
            @Override
            public void done(List<ParseTicket> tickets, ParseException e) {
                if (e == null) {
                    for (ParseTicket ticket : tickets)
                        mContext.getContentResolver().insert(Contract.Ticket.CONTENT_URI, ticket.getContentValues());
                } else {
                    mSyncFailed = true;
                    e.printStackTrace();
                }

                if (!mSyncFailed) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                    if (!sp.edit().putString(LAST_SYNC, Contract.DATE_TIME_FORMATTER.format(new Date())).commit())
                        Toast.makeText(mContext, "failed to write on disk", Toast.LENGTH_LONG).show();
                } else Toast.makeText(mContext, "Error fetching data to the server", Toast.LENGTH_LONG).show();
            }
        });
    }

}