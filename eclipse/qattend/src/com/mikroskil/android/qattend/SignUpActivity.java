package com.mikroskil.android.qattend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

    private Context mContext;
    private ProgressDialog mDialog;

    // Values for email and password at the time of the sign_in attempt.
    private String mUsername;
    private String mPassword;
    private String mName;
    private String mEmail;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mNameView;
    private EditText mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = this;

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mNameView = (EditText) findViewById(R.id.name);
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.ime_sign_up || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the sign_in form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual sign_in attempt is made.
     */
    public void attemptSignUp() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mEmailView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the sign_in attempt.
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mEmail = mEmailView.getText().toString();
        mName = mNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 3) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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

        if (TextUtils.isEmpty(mName)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (mName.length() < 3) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt sign_in and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage(getString(R.string.progress_signing_up));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();

            ParseUser user = new ParseUser();
            user.setUsername(mUsername);
            user.setPassword(mPassword);
            user.setEmail(mEmail);
            user.put("name", mName);
            user.put("orgCount", 0);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    mDialog.dismiss();

                    if (null != e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(mContext, DispatchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }

}
