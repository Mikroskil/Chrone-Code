package com.mikroskil.android.qattend;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.model.ParseEvent;
import com.mikroskil.android.qattend.db.model.ParseMember;
import com.mikroskil.android.qattend.db.model.ParseTicket;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;

public class EventDetailActivity extends Activity {

    public static final int REQ_ADD_ATTENDEE = 1;
    public static final int REQ_VERIFY_ATTENDEE = 2;
    public static final String ARG_QR_RAW = "qr_raw";
    public static final String ARG_SCAN_MODE = "SCAN_MODE";
    public static final String ARG_EVENT_ID = "event_id";

    private long mId;
    private String mObjId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_detail);

        mId = getIntent().getLongExtra(Contract.Event._ID, 0);
        Uri uri = Uri.parse(Contract.Event.CONTENT_URI + "/" + mId);

        findViewById(R.id.show_tickets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailActivity.this, TicketActivity.class);
                intent.putExtra(ARG_EVENT_ID, mObjId);
                startActivity(intent);
            }
        });

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                ParseEvent.Event event = ParseEvent.fromCursor(cursor);
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                mObjId = event.objId;
                setTitle(event.title);

                ((TextView) findViewById(R.id.title)).setText(event.title);
                ((TextView) findViewById(R.id.location)).setText(event.location);
                ((TextView) findViewById(R.id.time)).setText(formatter.format(event.startDate) + " - " + formatter.format(event.endDate));
                ((TextView) findViewById(R.id.ticket_count)).setText("Ticket Count: " + event.ticketCount);

                TextView statusView = ((TextView) findViewById(R.id.status));
                if (event.privacy) statusView.setText("Privacy: Public");
                else statusView.setText("Privacy: Private");

                TextView descView = (TextView) findViewById(R.id.desc);
                if (event.desc != null) descView.setText(event.desc);
                else descView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit_event:
                intent = new Intent(this, ManageEventActivity.class);
                intent.putExtra(MainActivity.EVENT_MODE, false);
                intent.putExtra(Contract.Event._ID, mId);
                startActivity(intent);
                return true;
            case R.id.action_add_attendee:
                intent = new Intent(this, ScannerActivity.class);
                intent.putExtra(ARG_SCAN_MODE, "QR_CODE_MODE");
                startActivityForResult(intent, REQ_ADD_ATTENDEE);
                return true;
            case R.id.action_verify_attendee:
                intent = new Intent(this, ScannerActivity.class);
                intent.putExtra(ARG_SCAN_MODE, "QR_CODE_MODE");
                startActivityForResult(intent, REQ_VERIFY_ATTENDEE);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ADD_ATTENDEE) {
            if (resultCode == RESULT_OK) {
                addAttendee(data);
//                String qrCode = data.getStringExtra("RESULT");
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQ_VERIFY_ATTENDEE) {
            if (resultCode == RESULT_OK) {
                verifyAttendee(data);
//                String qrCode = data.getStringExtra("RESULT");
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void verifyAttendee(Intent data) {
        Uri uri = Uri.parse(Contract.Membership.CONTENT_URI + "/" + data.getStringExtra(ARG_QR_RAW));
        Log.d(QattendApp.TAG, "rawCodeUri=" + uri);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                values.put(Contract.Ticket.COL_VERIFIED, true);

                int count = getContentResolver().update(Contract.Ticket.CONTENT_URI, values, Contract.Ticket.COL_PARTICIPANT + " = ?",
                        new String[] { cursor.getString(cursor.getColumnIndexOrThrow(Contract.Membership.COL_APPLICANT_FROM)) });

                if (count == 1) {
                    // TODO remove this block if sync already works
                    Cursor c = getContentResolver().query(Contract.Ticket.CONTENT_URI,
                            new String[] { Contract.Ticket.COL_OBJ_ID },
                            Contract.Ticket.COL_PARTICIPANT + " = ?",
                            new String[] { cursor.getString(cursor.getColumnIndexOrThrow(Contract.Membership.COL_APPLICANT_FROM)) },
                            null);
                    if (c != null && c.moveToFirst()) {
                        ParseTicket ticket = ParseObject.createWithoutData(ParseTicket.class, c.getString(c.getColumnIndexOrThrow(Contract.Ticket.COL_OBJ_ID)));
                        ticket.setVerified(true);
                        ticket.saveEventually();
                        c.close();
                    }

                    Toast.makeText(this, "Verified", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(this, "Unable to verify", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Your membership Id doesn't valid.", Toast.LENGTH_LONG).show();
        }
    }

    private void addAttendee(Intent data) {
        Uri uri = Uri.parse(Contract.Membership.CONTENT_URI + "/" + data.getStringExtra(ARG_QR_RAW));
        Log.d(QattendApp.TAG, "rawCodeUri = " + uri);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                ParseEvent event = ParseObject.createWithoutData(ParseEvent.class, mObjId);
                event.incTicketCount();

                ParseMember member = ParseObject.createWithoutData(ParseMember.class,
                        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Membership.COL_APPLICANT_FROM)));

                ParseTicket ticket = new ParseTicket();
                ticket.setParticipant(member);
                ticket.setParticipateTo(event);
                ticket.setVerified(false);
                ticket.saveEventually();

                getContentResolver().insert(Contract.Ticket.CONTENT_URI, ticket.getContentValues());

                // increment ticket count
                uri = Uri.parse(Contract.Event.CONTENT_URI + "/" + mObjId);
                getContentResolver().update(uri, null, null, null);

                Toast.makeText(this, "Succeed", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Your membership Id doesn't valid.", Toast.LENGTH_LONG).show();
        }
    }

}
