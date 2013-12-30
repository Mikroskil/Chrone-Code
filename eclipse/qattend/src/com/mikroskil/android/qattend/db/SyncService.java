package com.mikroskil.android.qattend.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mikroskil.android.qattend.QattendApp;

public class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true, false);
            }
        }
        Log.d(QattendApp.TAG, "SyncService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(QattendApp.TAG, "SyncService destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(QattendApp.TAG, "SyncService Binded");
        return sSyncAdapter.getSyncAdapterBinder();
    }

}
