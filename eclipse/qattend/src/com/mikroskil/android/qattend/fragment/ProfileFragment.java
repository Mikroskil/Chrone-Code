package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Space;
import android.widget.TextView;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.OrganizationDetailActivity;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.model.ParseMember;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;

    private Activity mContext;

    public ProfileFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(QattendApp.TAG, "query profile data");

        ParseMember user = (ParseMember) ParseUser.getCurrentUser();
        ((TextView) rootView.findViewById(R.id.name)).setText(user.getName());
        ((TextView) rootView.findViewById(R.id.username)).setText("@" + user.getUsername());
        ((TextView) rootView.findViewById(R.id.email)).setText(user.getEmail());
        ((TextView) rootView.findViewById(R.id.gender)).setText((user.getGender() ? "Male": "Female"));
        ((TextView) rootView.findViewById(R.id.phone)).setText(user.getPhone());
        ((TextView) rootView.findViewById(R.id.about)).setText(user.getAbout());

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        mAdapter = new SimpleCursorAdapter(mContext,
                R.layout.card_organization, null,
                new String[] { Contract.Organization.COL_NAME, Contract.Organization.COL_USERNAME },
                new int[] { R.id.name, R.id.username },
                0);

        Space space = new Space(mContext);
        listView.addFooterView(space, null, false);
        listView.addHeaderView(space, null, false);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Log.d(QattendApp.TAG, "pos=" + pos + " id=" + id);
                Intent intent = new Intent(mContext, OrganizationDetailActivity.class);
                intent.putExtra(Contract.Organization._ID, id);
                startActivity(intent);
            }
        });
        Log.d(QattendApp.TAG, "init profile loader");
        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Contract.Organization._ID,
                Contract.Organization.COL_NAME,
                Contract.Organization.COL_USERNAME
        };

        return new CursorLoader(mContext, Contract.Organization.CONTENT_URI,
                projection, null, null, Contract.Organization.COL_CREATED_AT + " DESC");
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
