package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Arrays;

public class ProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int position;
    private static View space;
    private View rootView;
    private ParseQueryAdapter<ParseObject> mAdapter;

    public ProfileFragment(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                ((TextView) rootView.findViewById(R.id.name)).setText(user.getString("name"));
                ((TextView) rootView.findViewById(R.id.username)).setText("@" + user.getUsername());
                ((TextView) rootView.findViewById(R.id.email)).setText(user.getEmail());
                ((TextView) rootView.findViewById(R.id.gender)).setText((user.getBoolean("gender") ? "Male": "Female"));
                ((TextView) rootView.findViewById(R.id.phone)).setText(user.getString("phone"));
                ((TextView) rootView.findViewById(R.id.about)).setText(user.getString("about"));
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_organization);
        mAdapter = new ParseQueryAdapter<ParseObject>(getActivity(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Organization");
                query.selectKeys(Arrays.asList("name", "username"));
                query.whereEqualTo("ownBy", ParseUser.getCurrentUser());
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
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

        space = new View(getActivity());
        listView.addFooterView(space, null, false);
        listView.addHeaderView(space, null, false);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "list " + i, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
