package com.mikroskil.android.qattend;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.QattendDatabase;
import com.mikroskil.android.qattend.db.model.ParseEvent;
import com.mikroskil.android.qattend.db.model.ParseMember;
import com.mikroskil.android.qattend.db.model.ParseMembership;
import com.mikroskil.android.qattend.db.model.ParseOrganization;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ParseAnalytics.trackAppOpened(getIntent());
        if (ParseUser.getCurrentUser() != null) new PrefetchData().execute();
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                }
            }, 1000);
        }
    }

    private class PrefetchData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(QattendApp.TAG, "prefetch data");

            QattendDatabase dbHelper = new QattendDatabase(SplashActivity.this);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.onUpgrade(db, QattendDatabase.DB_VERSION, QattendDatabase.DB_VERSION + 1);

            ParseQuery<ParseOrganization> orgQ = ParseQuery.getQuery(ParseOrganization.class);
            orgQ.whereEqualTo(Contract.Organization.COL_OWN_BY, ParseUser.getCurrentUser());

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
                                        long id = db.insert(Contract.Membership.TABLE, null, member.getContentValues());
                                        Log.d(QattendApp.TAG, "id=" + id + " " + member.getContentValues());

                                        ParseMember user = (ParseMember) member.getParseUser(Contract.Membership.COL_APPLICANT_FROM);

                                        id = db.insert(Contract.Member.TABLE, null, user.getContentValues());
                                        Log.d(QattendApp.TAG, "id=" + id + " " + user.getContentValues());
                                    }
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Log.e(QattendApp.TAG, "Splash Activity: " + e.getMessage());
                                    Toast.makeText(SplashActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        for (ParseOrganization org : orgs) {
                            long id = db.insert(Contract.Organization.TABLE, null, org.getContentValues());
                            Log.d(QattendApp.TAG, "id=" + id + " " + org.getContentValues());
                        }
//                        NavigationDrawerFragment.updateSpinnerAdapter();
                    } else {
                        Log.e(QattendApp.TAG, "Splash Activity: " + e.getMessage());
                        Toast.makeText(SplashActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            ParseQuery<ParseEvent> eventQ = ParseQuery.getQuery(ParseEvent.class);
            eventQ.findInBackground(new FindCallback<ParseEvent>() {
                @Override
                public void done(List<ParseEvent> events, ParseException e) {
                    if (e == null) {
                        for (ParseEvent event : events) {
                            long id = db.insert(Contract.Event.TABLE, null, event.getContentValues());
                            Log.d(QattendApp.TAG, "id=" + id + " " + event.getContentValues());
                        }

                    } else {
                        Log.e(QattendApp.TAG, "Splash Activity: " + e.getMessage());
                        Toast.makeText(SplashActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            return null;
        }
    }

}
