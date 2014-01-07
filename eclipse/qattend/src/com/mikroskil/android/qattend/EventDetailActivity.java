package com.mikroskil.android.qattend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.model.ParseEvent;

import java.text.SimpleDateFormat;

public class EventDetailActivity extends Activity {

    private long mId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_detail);

        mId = getIntent().getLongExtra(Contract.Event._ID, 0);
        Uri uri = Uri.parse(Contract.Event.CONTENT_URI + "/" + mId);

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                ParseEvent.Event event = ParseEvent.fromCursor(cursor);
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                setTitle(event.title);

                ((TextView) findViewById(R.id.title)).setText(event.title);
                ((TextView) findViewById(R.id.location)).setText(event.location);
                ((TextView) findViewById(R.id.time)).setText(formatter.format(event.startDate) + " - " + formatter.format(event.endDate));

                TextView statusView = ((TextView) findViewById(R.id.status));
                if (event.privacy) statusView.setText("Public");
                else statusView.setText("Private");

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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit_event:
                Intent intent = new Intent(this, ManageEventActivity.class);
                intent.putExtra(MainActivity.EVENT_MODE, false);
                intent.putExtra(Contract.Event._ID, mId);
                startActivity(intent);
                return true;
            case R.id.action_verify_attendee:
                intent = new Intent(this, ScannerActivity.class);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == 1) {
                String qrCode = data.getStringExtra("RESULT");
                Toast.makeText(this, "Scan Result: " + qrCode, Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

}
