package com.mikroskil.android.qattend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.encode.QRCodeEncoder;
import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.model.ParseOrganization;

public class OrganizationDetailActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organization_activity_detail);

        String id = getIntent().getStringExtra(Contract.Organization._ID);
        Uri uri = Uri.parse(Contract.Organization.CONTENT_URI + "/" + id);

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                ParseOrganization.Organization org = ParseOrganization.fromCursor(cursor);
                setTitle(org.name);

                ((TextView) findViewById(R.id.name)).setText(org.name);
                ((TextView) findViewById(R.id.username)).setText("@" + org.username);
                ((TextView) findViewById(R.id.member_count)).setText(String.valueOf(org.memberCount));

                TextView emailView = (TextView) findViewById(R.id.email);
                if (org.email != null) emailView.setText(org.email);
                else emailView.setVisibility(View.GONE);

                TextView aboutView = (TextView) findViewById(R.id.about);
                if (org.about != null) aboutView.setText(org.about);
                else aboutView.setVisibility(View.GONE);

                ImageView qrView = (ImageView) findViewById(R.id.qr_code);
                QRCodeEncoder encoder = new QRCodeEncoder(id, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 500);

                try {
                    Bitmap bitmap = encoder.encodeAsBitmap();
                    qrView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
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
