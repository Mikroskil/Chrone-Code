package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class MemberFragment extends ListFragment {

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
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int pos, long id) {
        super.onListItemClick(list, view, pos, id);
        Toast.makeText(mContext, "list " + pos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.member, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_member).getActionView();
        searchView.setSearchableInfo(((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE))
                .getSearchableInfo(mContext.getComponentName()));
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
