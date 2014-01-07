package com.mikroskil.android.qattend;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.model.ParseMember;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Activity which displays a sign_in screen to the user, offering registration as
 * well.
 */
public class SignInActivity extends AccountAuthenticatorActivity {

    public static final int REQ_SIGN_UP = 1;

    private String mUsername;
    private String mPassword;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mSubmitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSubmitView = (Button) findViewById(R.id.button_sign_in);

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

        mSubmitView.setOnClickListener(new View.OnClickListener() {
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
                startActivityForResult(new Intent(this, SignUpActivity.class), REQ_SIGN_UP);
                return true;
            case R.id.action_recover_password:
                startActivity(new Intent(this, RecoverPasswordActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(QattendApp.TAG, String.format("Data sent: req=%s res=%s data=%s", requestCode, resultCode, data));
        if (requestCode == REQ_SIGN_UP && resultCode == RESULT_OK) {
            mUsername = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            mPassword = data.getStringExtra(AccountManager.KEY_PASSWORD);
            signIn();
        }
        else super.onActivityResult(requestCode, resultCode, data);
    }

    private void attemptSignIn() {
        Log.d(QattendApp.TAG, "attempt sign in");

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        mUsername = mUsernameView.getText().toString().trim();
        mPassword = mPasswordView.getText().toString().trim();

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

        if (cancel) focusView.requestFocus();
        else {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mSubmitView.getWindowToken(), 0);
            signIn();
        }
    }

    private void signIn() {
        Log.d(QattendApp.TAG, "sign in");

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.progress_signing_in));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.show();

        ParseMember.logInInBackground(mUsername, mPassword, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                Log.d(QattendApp.TAG, "sign in callback");
                progress.dismiss();

                if (e == null) {
                    ParseMember me = (ParseMember) user;
                    me.setLastSignIn(new Date());
                    me.saveEventually();
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Log.e(QattendApp.TAG, e.getMessage());
                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
