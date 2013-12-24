package com.mikroskil.android.qattend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

/**
 * Activity which displays a sign_in screen to the user, offering registration as
 * well.
 */
public class SignInActivity extends Activity {

    private ProgressDialog mDialog;

    // Values for email and password at the time of the sign_in attempt.
    private String mUsername;
    private String mPassword;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.ime_sign_in || id == EditorInfo.IME_NULL) {
                    attemptSignIn();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_up:
                startActivity(new Intent(this, SignUpActivity.class));
                return true;
            case R.id.action_recover_password:
                Toast.makeText(this, R.string.action_recover_password, Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the sign_in form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual sign_in attempt is made.
     */
    public void attemptSignIn() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the sign_in attempt.
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

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
            mDialog.setMessage(getString(R.string.progress_signing_in));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();

            final Activity context = this;
            ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (null != e) {
                        mDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        user.put("lastSignIn", new Date());
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                mDialog.dismiss();
                                if (null != e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent = new Intent(context, DispatchActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

}
