package com.mikroskil.android.qattend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.model.ParseMember;
import com.mikroskil.android.qattend.db.model.ParseOrganization;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateOrganizationActivity extends Activity {

    private String mUsername;
    private String mName;

    private EditText mUsernameView;
    private EditText mNameView;
    private Button mSubmitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organization);

        mNameView = (EditText) findViewById(R.id.name);
        mUsernameView = (EditText) findViewById(R.id.username);
        mSubmitView = (Button) findViewById(R.id.button_create_organization);

        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.ime_create_organization || id == EditorInfo.IME_NULL) {
                    attemptCreateOrganization();
                    return true;
                }
                return false;
            }
        });

        mSubmitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateOrganization();
            }
        });
    }

    private void attemptCreateOrganization() {
        mUsernameView.setError(null);
        mNameView.setError(null);

        mUsername = mUsernameView.getText().toString().trim();
        mName = mNameView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mName)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (mName.length() < 3) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (mUsername.length() < 3) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) focusView.requestFocus();
        else {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mSubmitView.getWindowToken(), 0);
            createOrganization();
        }
    }

    private void createOrganization() {
        Log.d(QattendApp.TAG, "create organization");

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.progress_creating));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.show();

        final ParseOrganization org = new ParseOrganization();
        ParseMember user = (ParseMember) ParseUser.getCurrentUser();
        org.setName(mName);
        org.setUsername(mUsername);
        org.initializeMemberCount();
        user.increment(Contract.Member.COL_ORG_COUNT);
        org.setOwner(user);

        org.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(QattendApp.TAG, "create organization callback");
                progress.dismiss();

                if (e == null) {
                    getContentResolver().insert(Contract.Organization.CONTENT_URI, org.getContentValues());
                    Toast.makeText(CreateOrganizationActivity.this, "Organization created successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.e(QattendApp.TAG, "create organization: " + e.getMessage());
                    Toast.makeText(CreateOrganizationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
