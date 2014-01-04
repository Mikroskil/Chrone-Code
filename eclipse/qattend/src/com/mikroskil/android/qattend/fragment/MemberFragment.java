package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;

public class MemberFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;
    private Activity mContext;
    private int mPos;

    public MemberFragment() {}

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
                android.R.layout.simple_list_item_2,
                null,
                new String[] { Contract.Member.COL_NAME, Contract.Member.COL_USERNAME },
                new int[] { android.R.id.text1, android.R.id.text2 },
                0);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onListItemClick(ListView list, View view, int pos, long id) {
        super.onListItemClick(list, view, pos, id);
        Log.d(QattendApp.TAG, String.format("Member clicked: pos=%s, id=%s", pos, id));
        Toast.makeText(mContext, "pos=" + pos + " id=" + id, Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // send raw query snippet to content provider about 'approved' status.
        String selection = null;
        if (mPos == 4) selection = "WHERE A.%s = 1";
        else if (mPos == 5) selection = "WHERE A.%s = 0";

        return new CursorLoader(mContext, Contract.Member.CONTENT_URI,
                null, selection, null, null);
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
