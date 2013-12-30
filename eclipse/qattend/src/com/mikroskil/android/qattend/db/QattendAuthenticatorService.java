package com.mikroskil.android.qattend.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mikroskil.android.qattend.QattendApp;

public class QattendAuthenticatorService extends Service {

    private QattendAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new QattendAuthenticator(this);
        Log.d(QattendApp.TAG, "QattendAuthenticatorService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(QattendApp.TAG, "QattendAuthenticatorService destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
