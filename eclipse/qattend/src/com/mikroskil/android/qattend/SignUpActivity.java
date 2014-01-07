package com.mikroskil.android.qattend;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.model.ParseMember;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

    private String mName;
    private String mUsername;
    private String mEmail;
    private String mPassword;

    private EditText mNameView;
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mSubmitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mNameView = (EditText) findViewById(R.id.name);
        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSubmitView = (Button) findViewById(R.id.button_sign_up);

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

        mSubmitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
    }

    private void attemptSignUp() {
        Log.d(QattendApp.TAG, "attempt sign up");

        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mEmailView.setError(null);
        mNameView.setError(null);

        mUsername = mUsernameView.getText().toString().trim();
        mPassword = mPasswordView.getText().toString().trim();
        mEmail = mEmailView.getText().toString().trim();
        mName = mNameView.getText().toString().trim();

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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
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

        if (cancel) focusView.requestFocus();
        else {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mSubmitView.getWindowToken(), 0);
            signUp();
        }
    }

    private void signUp() {
        Log.d(QattendApp.TAG, "sign up");

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.progress_signing_up));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.show();

        ParseMember user = new ParseMember();
        user.setUsername(mUsername);
        user.setPassword(mPassword);
        user.setEmail(mEmail);
        user.setName(mName);
        user.initializeOrgCount();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(QattendApp.TAG, "sign up callback");
                progress.dismiss();

                if (e == null) {
                    Intent data = new Intent();
                    data.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
                    data.putExtra(AccountManager.KEY_PASSWORD, mPassword);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Log.e(QattendApp.TAG, e.getMessage());
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
