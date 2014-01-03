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

import com.mikroskil.android.qattend.fragment.ProfileFragment;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

public class CreateOrganizationActivity extends Activity {

    private ProgressDialog mProgress;

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
            mProgress = new ProgressDialog(this);
            mProgress.setMessage(getString(R.string.progress_creating));
            mProgress.setIndeterminate(false);
            mProgress.setCancelable(false);
            mProgress.show();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("name", mName);
            params.put("username", mUsername);

            ParseCloud.callFunctionInBackground("createOrganization", params, new FunctionCallback<String>() {
                @Override
                public void done(String result, ParseException e) {
                    mProgress.dismiss();
                    if (null == e) {
                        NavigationDrawerFragment.updateSpinnerAdapter();
                        ProfileFragment.updateAdapter();
                        Toast.makeText(CreateOrganizationActivity.this, result, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Log.e(QattendApp.TAG, "create organization: " + e.getMessage());
                        Toast.makeText(CreateOrganizationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
