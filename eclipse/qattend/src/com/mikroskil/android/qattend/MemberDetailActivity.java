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

import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.model.ParseMember;

public class MemberDetailActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_activity_detail);

        Uri uri = Uri.parse(Contract.Member.CONTENT_URI + "/" + getIntent().getLongExtra(Contract.Member._ID, 0));

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                ParseMember.Member member = ParseMember.fromCursor(cursor);
                setTitle(member.name);

                ((TextView) findViewById(R.id.name)).setText(member.name);
                ((TextView) findViewById(R.id.username)).setText("@" + member.username);
                ((TextView) findViewById(R.id.email)).setText(member.email);

                TextView genderView = ((TextView) findViewById(R.id.gender));
                if (member.gender) genderView.setText("Male");
                else genderView.setText("Female");

                TextView aboutView = (TextView) findViewById(R.id.about);
                if (member.about != null) aboutView.setText(member.about);
                else aboutView.setVisibility(View.GONE);

                TextView phoneView = (TextView) findViewById(R.id.phone);
                if (member.phone != null) phoneView.setText(member.phone);
                else phoneView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
