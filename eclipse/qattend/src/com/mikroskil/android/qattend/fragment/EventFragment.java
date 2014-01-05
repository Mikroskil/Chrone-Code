package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mikroskil.android.qattend.EventDetailActivity;
import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.NavigationDrawerFragment;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;
    private Activity mContext;
    private int mPos;

    public EventFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPos = getArguments().getInt(MainActivity.ARG_SECTION_NUMBER);
        mContext = activity;
        ((MainActivity) activity).onSectionAttached(mPos);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(mContext,
                R.layout.card_event,
                null,
                new String[] { Contract.Event.COL_TITLE, Contract.Event.COL_START_DATE, Contract.Event.COL_LOCATION },
                new int[] { R.id.title, R.id.time, R.id.location },
                0);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == cursor.getColumnIndexOrThrow(Contract.Event.COL_START_DATE)) {
                    Date startDate = null;
                    Date endDate = null;
                    SimpleDateFormat parser = new SimpleDateFormat(Contract.DATE_FORMAT);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, HH:mm");
                    try {
                        startDate = parser.parse(cursor.getString(i));
                        endDate = parser.parse(cursor.getString(i + 1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((TextView) view).setText(formatter.format(startDate) + " - " + formatter.format(endDate));
                    return true;
                }
                return false;
            }
        });

        // List view not shown properly when add these.
//        Space space = new Space(mContext);
//        getListView().addFooterView(space, null, false);
//        getListView().addHeaderView(space, null, false);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onListItemClick(ListView list, View view, int pos, long id) {
        super.onListItemClick(list, view, pos, id);
        Log.d(QattendApp.TAG, String.format("Event clicked: pos=%s, id=%s", pos, id));
        Cursor cursor = (Cursor) mAdapter.getItem(pos);
        Intent intent = new Intent(mContext, EventDetailActivity.class);
        intent.putExtra(Contract.Event._ID, cursor.getString(cursor.getColumnIndexOrThrow(Contract.Event._ID)));
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Contract.Event._ID,
                Contract.Event.COL_TITLE,
                Contract.Event.COL_START_DATE,
                Contract.Event.COL_END_DATE,
                Contract.Event.COL_LOCATION
        };
        String selection = null;

        if (mPos == 1) {
            selection = String.format("%s=? AND DATETIME('NOW') BETWEEN %s AND %s",
                    Contract.Event.COL_HOST_BY,
                    Contract.Event.COL_START_DATE,
                    Contract.Event.COL_END_DATE);
        }
        else if (mPos == 2) {
            selection = String.format("%s=? AND DATETIME('NOW') < %s",
                    Contract.Event.COL_HOST_BY,
                    Contract.Event.COL_START_DATE);
        }
        else if (mPos == 3) {
            selection = String.format("%s=? AND DATETIME('NOW') > %s",
                    Contract.Event.COL_HOST_BY,
                    Contract.Event.COL_END_DATE);
        }

        return new CursorLoader(mContext, Contract.Event.CONTENT_URI,
                projection, selection,
                new String[] { NavigationDrawerFragment.getActiveOrgId() },
                Contract.Event.COL_CREATED_AT + " DESC");
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
