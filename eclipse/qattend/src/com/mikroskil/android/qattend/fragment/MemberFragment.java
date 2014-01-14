package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.MemberDetailActivity;
import com.mikroskil.android.qattend.NavigationDrawerFragment;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;

public class MemberFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private CustomSimpleCursorAdapter mAdapter;
    private Activity mContext;
    private int mPos;

    public MemberFragment() {}

    public static MemberFragment newInstance(int pos) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, pos);
        MemberFragment f = new MemberFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPos = getArguments().getInt(ARG_SECTION_NUMBER);
        mContext = activity;
        ((MainActivity) activity).onSectionAttached(mPos);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new CustomSimpleCursorAdapter(mContext,
                android.R.layout.simple_list_item_2,
                null,
                new String[] { Contract.Member.COL_NAME, Contract.Member.COL_USERNAME },
                new int[] { android.R.id.text1, android.R.id.text2 },
                0);

        setListAdapter(mAdapter);

        Log.d(QattendApp.TAG, "init member loader");
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String orgId = NavigationDrawerFragment.getActiveOrgId();
        if (orgId == null) return null;
        // send raw query snippet to content provider about 'approved' status.
        String selection = null;
        if (mPos == 4) {
            selection = "WHERE A.%s = 1";
            mAdapter.setViewResource(android.R.layout.simple_list_item_2);
        }
        else if (mPos == 5) {
            selection = "WHERE A.%s = 0";
            mAdapter.setViewResource(R.layout.action_list);
        }

        return new CursorLoader(mContext, Contract.Member.CONTENT_URI,
                null, selection, new String[] { orgId }, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {

        private int mLayout;

        public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void setViewResource(int layout) {
            super.setViewResource(layout);
            mLayout = layout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            final int pos = position;
            final long id = getItemId(position);

            if (mLayout == R.layout.action_list) {
                v.findViewById(R.id.item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(QattendApp.TAG, String.format("Member clicked: pos=%s, id=%s", pos, id));
                        Intent intent = new Intent(mContext, MemberDetailActivity.class);
                        intent.putExtra(Contract.Member._ID, id);
                        startActivity(intent);
                    }
                });

                v.findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(QattendApp.TAG, String.format("Member clicked: pos=%s, id=%s", pos, id));

                        // TODO sync the update with server because this block just update locally.
                        Uri uri = Uri.parse(Contract.Member.CONTENT_URI + "/" + id);
                        Cursor c = mContext.getContentResolver().query(uri, null, null, null, null);
                        if (c != null && c.moveToFirst()) {
                            ContentValues values = new ContentValues();
                            values.put(Contract.Membership.COL_APPROVED, true);
                            int count = mContext.getContentResolver().update(Contract.Membership.CONTENT_URI, values,
                                    Contract.Membership.COL_APPLICANT_FROM + " = ?",
                                    new String[] { c.getString(c.getColumnIndexOrThrow(Contract.Member.COL_OBJ_ID)) });
                            if (count != 0) {
                                getLoaderManager().getLoader(0).forceLoad();
                                Toast.makeText(getActivity(), "approved!", Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(getActivity(), "nothing changed!", Toast.LENGTH_SHORT).show();
                            c.close();
                        } else {
                            Toast.makeText(getActivity(), "failed to approve", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (mLayout == android.R.layout.simple_list_item_2) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(QattendApp.TAG, String.format("Member clicked: pos=%s, id=%s", pos, id));
                        Intent intent = new Intent(mContext, MemberDetailActivity.class);
                        intent.putExtra(Contract.Member._ID, id);
                        startActivity(intent);
                    }
                });
            }

            return v;
        }
    }
}
