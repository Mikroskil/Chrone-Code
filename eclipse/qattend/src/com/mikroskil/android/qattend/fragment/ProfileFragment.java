package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.Toast;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private static SimpleCursorAdapter mAdapter;

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

        ParseUser user = ParseUser.getCurrentUser();
        ((TextView) rootView.findViewById(R.id.name)).setText(user.getString("name"));
        ((TextView) rootView.findViewById(R.id.username)).setText("@" + user.getUsername());
        ((TextView) rootView.findViewById(R.id.email)).setText(user.getEmail());
        ((TextView) rootView.findViewById(R.id.gender)).setText((user.getBoolean("gender") ? "Male": "Female"));
        ((TextView) rootView.findViewById(R.id.phone)).setText(user.getString("phone"));
        ((TextView) rootView.findViewById(R.id.about)).setText(user.getString("about"));

        String[] projection = {
                Contract.Organization._ID,
                Contract.Organization.COL_NAME,
                Contract.Organization.COL_USERNAME
        };

        Cursor cursor = mContext.getContentResolver().query(Contract.Organization.CONTENT_URI,
                projection, null, null, Contract.Organization.COL_CREATED_AT + " DESC");

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        mAdapter = new SimpleCursorAdapter(mContext,
                R.layout.card_organization, cursor,
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
                Toast.makeText(mContext, "pos=" + pos + " id=" + id, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public static void updateAdapter() {
        mAdapter.notifyDataSetChanged();
    }

}
