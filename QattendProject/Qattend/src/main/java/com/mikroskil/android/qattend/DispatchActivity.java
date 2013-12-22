package com.mikroskil.android.qattend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != ParseUser.getCurrentUser()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

}
