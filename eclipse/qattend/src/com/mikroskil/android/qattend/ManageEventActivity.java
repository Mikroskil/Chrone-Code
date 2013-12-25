package com.mikroskil.android.qattend;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ManageEventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        final LayoutInflater inflater = (LayoutInflater) actionBar
                .getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View actionBarView = inflater.inflate(R.layout.action_bar_dialog, null);
        actionBarView.findViewById(R.id.action_bar_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getBooleanExtra("EVENT_MODE", false)) {
                    // create event
                }
                else {
                    // edit event
                }
                finish();
            }
        });
        actionBarView.findViewById(R.id.action_bar_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(R.layout.activity_manage_event);
    }

}
