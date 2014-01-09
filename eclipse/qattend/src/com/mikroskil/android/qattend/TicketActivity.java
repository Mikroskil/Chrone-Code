package com.mikroskil.android.qattend;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import com.mikroskil.android.qattend.db.Contract;

public class TicketActivity extends ListActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;
    private String mEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        mEventId = getIntent().getStringExtra(EventDetailActivity.ARG_EVENT_ID);

        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, null,
                new String[] { Contract.Member.COL_NAME, Contract.Member.COL_USERNAME },
                new int[] { android.R.id.text1, android.R.id.text2 }, 0);

        setListAdapter(mAdapter);

        Log.d(QattendApp.TAG, "init ticket loader");
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Contract.Ticket.CONTENT_URI, null, null, new String[] { mEventId }, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

}
