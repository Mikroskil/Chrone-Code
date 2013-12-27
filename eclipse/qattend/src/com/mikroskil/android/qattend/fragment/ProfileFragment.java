package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.CreateOrganizationActivity;
import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static ParseQueryAdapter<ParseObject> mAdapter;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                try {
                    ((TextView) rootView.findViewById(R.id.name)).setText(user.getString("name"));
                    ((TextView) rootView.findViewById(R.id.username)).setText("@" + user.getUsername());
                    ((TextView) rootView.findViewById(R.id.email)).setText(user.getEmail());
                    ((TextView) rootView.findViewById(R.id.gender)).setText((user.getBoolean("gender") ? "Male": "Female"));
                    ((TextView) rootView.findViewById(R.id.phone)).setText(user.getString("phone"));
                    ((TextView) rootView.findViewById(R.id.about)).setText(user.getString("about"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        mAdapter = new ParseQueryAdapter<ParseObject>(mContext, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Organization");
                query.selectKeys(Arrays.asList("name", "username"));
                query.whereEqualTo("ownBy", ParseUser.getCurrentUser());
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                return query;
            }
        }) {
            @Override
            public View getItemView(ParseObject obj, View v, ViewGroup parent) {
                if (null == v) {
                    v = View.inflate(getContext(), R.layout.card_organization, null);
                }

                super.getItemView(obj, v, parent);
                ((TextView) v.findViewById(R.id.name)).setText(obj.getString("name"));
                ((TextView) v.findViewById(R.id.username)).setText("@" + obj.getString("username"));
                return v;
            }
        };
        mAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            @Override
            public void onLoading() {
                mContext.setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onLoaded(List<ParseObject> parseObjects, Exception e) {
                mContext.setProgressBarIndeterminateVisibility(false);
            }
        });

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_organization:
                startActivity(new Intent(mContext, CreateOrganizationActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void updateView() {
        mAdapter.loadObjects();
    }

}
