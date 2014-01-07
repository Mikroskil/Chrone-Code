package com.mikroskil.android.qattend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mikroskil.android.qattend.db.model.ParseMember;
import com.parse.ParseAnalytics;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ParseAnalytics.trackAppOpened(getIntent());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ParseMember.getCurrentUser() != null)
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                finish();
            }
        }, 1000);
    }

}
