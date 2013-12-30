package com.mikroskil.android.qattend.db;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.mikroskil.android.qattend.QattendApp;

class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
        Log.d(QattendApp.TAG, "onPerformSync");
    }

}
