package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.R;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class MemberFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static ParseQueryAdapter<ParseUser> mAdapter;

    private Activity mContext;
    private int mPos;

    public MemberFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPos = getArguments().getInt(ARG_SECTION_NUMBER);
        mContext = activity;
        ((MainActivity) activity).onSectionAttached(mPos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_member, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        mAdapter = new ParseQueryAdapter<ParseUser>(mContext, new ParseQueryAdapter.QueryFactory<ParseUser>() {
            @Override
            public ParseQuery<ParseUser> create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.selectKeys(Arrays.asList("name", "username"));
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                return query;
            }
        }) {
            @Override
            public View getItemView(ParseUser obj, View v, ViewGroup parent) {
                if (null == v) {
                    v = View.inflate(getContext(), android.R.layout.simple_list_item_2, null);
                }

                super.getItemView(obj, v, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setText(obj.getString("name"));
                ((TextView) v.findViewById(android.R.id.text2)).setText("@" + obj.getString("username"));
                return v;
            }
        };
        mAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
            @Override
            public void onLoading() {
                mContext.setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onLoaded(List<ParseUser> parseObjects, Exception e) {
                mContext.setProgressBarIndeterminateVisibility(false);
            }
        });

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
        inflater.inflate(R.menu.member, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_member).getActionView();
        searchView.setSearchableInfo(((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE))
                .getSearchableInfo(mContext.getComponentName()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_member:
                mContext.onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
