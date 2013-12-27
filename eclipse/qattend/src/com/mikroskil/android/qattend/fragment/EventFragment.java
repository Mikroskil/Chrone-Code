package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mikroskil.android.qattend.DetailActivity;
import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Arrays;
import java.util.List;

public class EventFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static ParseQueryAdapter<ParseObject> mAdapter;

    private Activity mContext;
    private int mPos;

    public EventFragment() {}

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ParseQueryAdapter<ParseObject>(mContext, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
                query.selectKeys(Arrays.asList("title", "location"));
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                return new ParseQuery<ParseObject>("Event");
            }
        }) {
            @Override
            public View getItemView(ParseObject obj, View v, ViewGroup parent) {
                if (null == v) {
                    v = View.inflate(getContext(), R.layout.card_event, null);
                }

                super.getItemView(obj, v, parent);
                ((TextView) v.findViewById(R.id.location)).setText(obj.getString("location"));
                ((TextView) v.findViewById(R.id.title)).setText(obj.getString("title"));
                ((TextView) v.findViewById(R.id.time)).setText(obj.getCreatedAt().toString());
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
        getListView().addFooterView(space, null, false);
        getListView().addHeaderView(space, null, false);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int pos, long id) {
        super.onListItemClick(list, view, pos, id);
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("LIST_INDEX", pos);
        startActivity(intent);
    }

}
