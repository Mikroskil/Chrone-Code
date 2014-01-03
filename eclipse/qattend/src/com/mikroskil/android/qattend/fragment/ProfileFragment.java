package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.QattendApp;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.QattendDatabase;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static SimpleCursorAdapter mAdapter;

    private Activity mContext;
    private static View rootView;

    public ProfileFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(QattendApp.TAG, "query profile data");

        ParseUser user = ParseUser.getCurrentUser();
        ((TextView) rootView.findViewById(R.id.name)).setText(user.getString("name"));
        ((TextView) rootView.findViewById(R.id.username)).setText("@" + user.getUsername());
        ((TextView) rootView.findViewById(R.id.email)).setText(user.getEmail());
        ((TextView) rootView.findViewById(R.id.gender)).setText((user.getBoolean("gender") ? "Male": "Female"));
        ((TextView) rootView.findViewById(R.id.phone)).setText(user.getString("phone"));
        ((TextView) rootView.findViewById(R.id.about)).setText(user.getString("about"));

        QattendDatabase dbHelper = new QattendDatabase(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT %s, %s, %s FROM %s ORDER BY %s",
                Contract.Organization._ID, Contract.Organization.COL_NAME, Contract.Organization.COL_USERNAME,
                Contract.Organization.TABLE, Contract.Organization.COL_CREATED_AT), null);

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        mAdapter = new SimpleCursorAdapter(mContext,
                R.layout.card_organization,
                cursor,
                new String[] { Contract.Organization.COL_NAME, Contract.Organization.COL_USERNAME },
                new int[] { R.id.name, R.id.username },
                0);

        View space = new View(mContext);
        listView.addFooterView(space, null, false);
        listView.addHeaderView(space, null, false);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(mContext, "list " + i, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public static void updateAdapter() {
        mAdapter.notifyDataSetChanged();
    }

}
