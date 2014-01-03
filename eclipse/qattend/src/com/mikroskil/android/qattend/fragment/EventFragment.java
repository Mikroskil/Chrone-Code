package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mikroskil.android.qattend.DetailActivity;
import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.NavigationDrawerFragment;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.QattendDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventFragment extends ListFragment {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(QattendApp.TAG, "query event data");

        mAdapter = new SimpleCursorAdapter(mContext,
                R.layout.card_event,
                getCursor(),
                new String[] { Contract.Event.COL_TITLE, Contract.Event.COL_START_DATE, Contract.Event.COL_LOCATION },
                new int[] { R.id.title, R.id.time, R.id.location },
                0);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == cursor.getColumnIndex(Contract.Event.COL_START_DATE)) {
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
        View space = new View(mContext);
        getListView().addFooterView(space, null, false);
        getListView().addHeaderView(space, null, false);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int pos, long id) {
        super.onListItemClick(list, view, pos, id);
        Cursor cursor = (Cursor) mAdapter.getItem(pos - 1);
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(Contract.Event._ID, cursor.getString(cursor.getColumnIndex(Contract.Event._ID)));
        startActivity(intent);
        Log.d(QattendApp.TAG, String.format("pos=%s, id=%s, objid=%s", pos, id, cursor.getString(cursor.getColumnIndex(Contract.Event._ID))));
    }

    private Cursor getCursor() {
        QattendDatabase dbHelper = new QattendDatabase(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (mPos == 1) {
            return db.rawQuery(String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = '%s' AND DATETIME('NOW') BETWEEN %s AND %s ORDER BY %s DESC",
                    Contract.Event._ID, Contract.Event.COL_TITLE, Contract.Event.COL_START_DATE,
                    Contract.Event.COL_END_DATE, Contract.Event.COL_LOCATION, Contract.Event.TABLE,
                    Contract.Event.COL_HOST_BY, NavigationDrawerFragment.getActiveOrgId(),
                    Contract.Event.COL_START_DATE, Contract.Event.COL_END_DATE,
                    Contract.Event.COL_CREATED_AT), null);
        }
        else if (mPos == 2) {
            return db.rawQuery(String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = '%s' AND DATETIME('NOW') < %s ORDER BY %s DESC",
                    Contract.Event._ID, Contract.Event.COL_TITLE, Contract.Event.COL_START_DATE,
                    Contract.Event.COL_END_DATE, Contract.Event.COL_LOCATION, Contract.Event.TABLE,
                    Contract.Event.COL_HOST_BY, NavigationDrawerFragment.getActiveOrgId(),
                    Contract.Event.COL_START_DATE,
                    Contract.Event.COL_CREATED_AT), null);
        }
        else if (mPos == 3) {
            return db.rawQuery(String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = '%s' AND DATETIME('NOW') > %s ORDER BY %s DESC",
                    Contract.Event._ID, Contract.Event.COL_TITLE, Contract.Event.COL_START_DATE,
                    Contract.Event.COL_END_DATE, Contract.Event.COL_LOCATION, Contract.Event.TABLE,
                    Contract.Event.COL_HOST_BY, NavigationDrawerFragment.getActiveOrgId(),
                    Contract.Event.COL_END_DATE,
                    Contract.Event.COL_CREATED_AT), null);
        }
        return null;
    }

}
