package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;

public class TicketFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_EVENT_ID = "event_id";
    private SimpleCursorAdapter mAdapter;
    private Context mContext;

    public TicketFragment() {}

    public static TicketFragment newInstance(int pos, String eventId) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, pos);
        args.putString(ARG_EVENT_ID, eventId);
        TicketFragment f = new TicketFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(mContext,
                android.R.layout.simple_list_item_2,
                null,
                new String[] { Contract.Member.COL_NAME, Contract.Member.COL_USERNAME },
                new int[] { android.R.id.text1, android.R.id.text2 },
                0);

        setListAdapter(mAdapter);

        Log.d(QattendApp.TAG, "init ticket loader");
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String selection;
        int section = bundle.getInt(ARG_SECTION_NUMBER);

        if (section == 1) selection = "AND T." + Contract.Ticket.COL_VERIFIED + " = 1 ";
        else if (section == 2) selection = "AND T." + Contract.Ticket.COL_VERIFIED + " = 0 ";
        else selection = "";

        return new CursorLoader(mContext, Contract.Ticket.CONTENT_URI, null, selection, new String[] { bundle.getString(ARG_EVENT_ID) }, null);
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
