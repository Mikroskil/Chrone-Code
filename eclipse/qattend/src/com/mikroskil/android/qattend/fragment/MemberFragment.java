package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.NavigationDrawerFragment;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.QattendDatabase;

public class MemberFragment extends ListFragment {

    private static SimpleCursorAdapter mAdapter;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(QattendApp.TAG, "query member data");

        mAdapter = new SimpleCursorAdapter(mContext,
                android.R.layout.simple_list_item_2,
                getCursor(),
                new String[] { Contract.Member.COL_NAME, Contract.Member.COL_USERNAME },
                new int[] { android.R.id.text1, android.R.id.text2 },
                0);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int pos, long id) {
        super.onListItemClick(list, view, pos, id);
        Toast.makeText(mContext, "list " + pos, Toast.LENGTH_SHORT).show();
    }

    private Cursor getCursor() {
        QattendDatabase dbHelper = new QattendDatabase(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (mPos == 4) {
            return db.rawQuery(String.format("SELECT A.%s, B.%s, B.%s FROM %s AS A INNER JOIN %s AS B ON A.%s = B.%s WHERE A.%s = 1 AND A.%s = '%s' ORDER BY A.%s DESC",
                    Contract.Membership._ID, Contract.Member.COL_NAME, Contract.Member.COL_USERNAME,
                    Contract.Membership.TABLE, Contract.Member.TABLE,
                    Contract.Membership.COL_APPLICANT_FROM, Contract.Member._ID, Contract.Membership.COL_APPROVED,
                    Contract.Membership.COL_APPLY_TO, NavigationDrawerFragment.getActiveOrgId(),
                    Contract.Membership.COL_CREATED_AT), null);
        } else if (mPos == 5) {
            return db.rawQuery(String.format("SELECT A.%s, B.%s, B.%s FROM %s AS A INNER JOIN %s AS B ON A.%s = B.%s WHERE A.%s = 0 AND A.%s = '%s' ORDER BY A.%s DESC",
                    Contract.Membership._ID, Contract.Member.COL_NAME, Contract.Member.COL_USERNAME,
                    Contract.Membership.TABLE, Contract.Member.TABLE,
                    Contract.Membership.COL_APPLICANT_FROM, Contract.Member._ID, Contract.Membership.COL_APPROVED,
                    Contract.Membership.COL_APPLY_TO, NavigationDrawerFragment.getActiveOrgId(),
                    Contract.Membership.COL_CREATED_AT), null);
        }
        return null;
    }

}
