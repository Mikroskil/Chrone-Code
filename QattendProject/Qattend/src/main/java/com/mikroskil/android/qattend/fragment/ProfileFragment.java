package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikroskil.android.qattend.CreateOrganizationActivity;
import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.R;
import com.mikroskil.android.qattend.SettingsActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int position;
    private View rootView;

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

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
