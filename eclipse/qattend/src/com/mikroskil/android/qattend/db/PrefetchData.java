package com.mikroskil.android.qattend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import com.parse.ParseQuery;

import java.util.List;

public class PrefetchData extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public PrefetchData(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(QattendApp.TAG, "prefetch data");

        QattendDatabase dbHelper = new QattendDatabase(mContext);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, QattendDatabase.DB_VERSION, QattendDatabase.DB_VERSION + 1);

        ParseQuery<ParseOrganization> orgQ = ParseQuery.getQuery(ParseOrganization.class);
        orgQ.whereEqualTo(Contract.Organization.COL_OWN_BY, ParseMember.getCurrentUser());

        orgQ.findInBackground(new FindCallback<ParseOrganization>() {
            @Override
            public void done(List<ParseOrganization> orgs, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseMembership> memberQ = ParseQuery.getQuery(ParseMembership.class);
                    memberQ.whereContainedIn(Contract.Membership.COL_APPLY_TO, orgs);
                    memberQ.include(Contract.Membership.COL_APPLICANT_FROM);
                    memberQ.findInBackground(new FindCallback<ParseMembership>() {
                        @Override
                        public void done(List<ParseMembership> members, ParseException e) {
                            if (e == null) {
                                for (ParseMembership member : members) {
                                    mContext.getContentResolver().insert(Contract.Membership.CONTENT_URI, member.getContentValues());
                                    ParseMember user = (ParseMember) member.getParseUser(Contract.Membership.COL_APPLICANT_FROM);
                                    mContext.getContentResolver().insert(Contract.Member.CONTENT_URI, user.getContentValues());
                                }
                            } else {
                                Log.e(QattendApp.TAG, "Splash Activity: " + e.getMessage());
                                Toast.makeText(mContext, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    ParseQuery<ParseEvent> eventQ = ParseQuery.getQuery(ParseEvent.class);
                    eventQ.whereContainedIn(Contract.Event.COL_HOST_BY, orgs);
                    eventQ.findInBackground(new FindCallback<ParseEvent>() {
                        @Override
                        public void done(List<ParseEvent> events, ParseException e) {
                            if (e == null) {
                                ParseQuery<ParseTicket> ticketQ = ParseQuery.getQuery(ParseTicket.class);
                                ticketQ.whereContainedIn(Contract.Ticket.COL_PARTICIPATE_TO, events);
                                ticketQ.findInBackground(new FindCallback<ParseTicket>() {
                                    @Override
                                    public void done(List<ParseTicket> tickets, ParseException e) {
                                        if (e == null) {
                                            for (ParseTicket ticket : tickets)
                                                mContext.getContentResolver().insert(Contract.Ticket.CONTENT_URI, ticket.getContentValues());
                                        } else {
                                            Log.e(QattendApp.TAG, "Splash Activity: " + e.getMessage());
                                            Toast.makeText(mContext, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                for (ParseEvent event : events)
                                    mContext.getContentResolver().insert(Contract.Event.CONTENT_URI, event.getContentValues());
                            } else {
                                Log.e(QattendApp.TAG, "Splash Activity: " + e.getMessage());
                                Toast.makeText(mContext, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    for (ParseOrganization org : orgs)
                        mContext.getContentResolver().insert(Contract.Organization.CONTENT_URI, org.getContentValues());
                } else {
                    Log.e(QattendApp.TAG, "Splash Activity: " + e.getMessage());
                    Toast.makeText(mContext, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return null;
    }
}