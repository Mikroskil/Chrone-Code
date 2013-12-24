package com.mikroskil.android.qattend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.fragment.ProfileFragment;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;

public class CreateOrganizationActivity extends Activity {
    private static final String ORG_KEY = "org";

    private ProgressDialog mDialog;

    // Values for email and password at the time of the sign_in attempt.
    private String mUsername;
    private String mName;

    // UI references.
    private EditText mUsernameView;
    private EditText mNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organization);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mNameView = (EditText) findViewById(R.id.name);
        mUsernameView = (EditText) findViewById(R.id.username);

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

        findViewById(R.id.button_create_organization).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateOrganization();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptCreateOrganization() {
        // Reset errors.
        mUsernameView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the sign_in attempt.
        mUsername = mUsernameView.getText().toString();
        mName = mNameView.getText().toString();

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

        if (cancel) {
            // There was an error; don't attempt sign_in and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage(getString(R.string.progress_creating));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("name", mName);
            params.put("username", mUsername);

            final Activity context = this;
            ParseCloud.callFunctionInBackground("createOrganization", params, new FunctionCallback<String>() {
                @Override
                public void done(String result, ParseException e) {
                    if (null == e) {
                        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                mDialog.dismiss();
                                if (null == e) {
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(ORG_KEY + ParseUser.getCurrentUser().getInt("orgCount"), mName);
                                    if(!editor.commit()) {
                                        Toast.makeText(context, "Failed to write on disk", Toast.LENGTH_LONG).show();
                                    }
                                    NavigationDrawerFragment.updateSpinnerAdapter(mName);
                                }
                                else {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                ProfileFragment.updateView();
                                finish();
                            }
                        });
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
