package com.mikroskil.android.qattend;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.PrefetchData;
import com.mikroskil.android.qattend.fragment.EventFragment;
import com.mikroskil.android.qattend.fragment.MemberFragment;
import com.mikroskil.android.qattend.fragment.ProfileFragment;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String ARG_SECTION_NUMBER = "sectionNumber";
    public static final String KEY_ORG_POS = "organizationPosition";
    public static final String EVENT_MODE = "event_mode";

    private static String[] sectionMenus;

    private int mOrgPos;
    private int mPos;
    private boolean prefetched = false;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        mTitle = getTitle();
        sectionMenus = getResources().getStringArray(R.array.section_menus);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (!prefetched) {
            new PrefetchData(this).execute();
            prefetched = true;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mPos = position;

        // update the main content by replacing fragments
        if (position == 0) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position);
            ProfileFragment fragment = new ProfileFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
        else if (position >= 1 && position <= 3) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position);
            EventFragment fragment = new EventFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
        else if (position >= 4 && position <= 5) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position);
            MemberFragment fragment = new MemberFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    public void onSectionAttached(int number) {
        mTitle = sectionMenus[number];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            MenuInflater inflater = getMenuInflater();
            if (mPos == 0) {
                inflater.inflate(R.menu.profile, menu);
            }
            else if (mPos >= 1 && mPos <= 3) {
                inflater.inflate(R.menu.event, menu);
                SearchView searchView = (SearchView) menu.findItem(R.id.action_search_event).getActionView();
                searchView.setSearchableInfo(((SearchManager) this.getSystemService(Context.SEARCH_SERVICE))
                        .getSearchableInfo(this.getComponentName()));
            }
            else if (mPos >= 4 && mPos <= 5) {
                inflater.inflate(R.menu.member, menu);
                SearchView searchView = (SearchView) menu.findItem(R.id.action_search_member).getActionView();
                searchView.setSearchableInfo(((SearchManager) this.getSystemService(Context.SEARCH_SERVICE))
                        .getSearchableInfo(this.getComponentName()));
            }
            else {
                inflater.inflate(R.menu.global, menu);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_organization:
                startActivity(new Intent(this, CreateOrganizationActivity.class));
                return true;
            case R.id.action_create_event:
                Intent intent = new Intent(this, ManageEventActivity.class);
                intent.putExtra(EVENT_MODE, true);
                startActivity(intent);
                return true;
            case R.id.action_search_event:
                this.onSearchRequested();
                return true;
            case R.id.action_search_member:
                this.onSearchRequested();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!writeInstanceState())
            Toast.makeText(this, "Failed to write instance state!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (readInstanceState() && mOrgPos != -1) {
            NavigationDrawerFragment.setSpinnerState(mOrgPos);
        }
    }

    public boolean writeInstanceState() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_ORG_POS, NavigationDrawerFragment.getSpinnerState());
        return editor.commit();
    }

    public boolean readInstanceState() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mOrgPos = sp.getInt(KEY_ORG_POS, -1);
        return sp.contains(KEY_ORG_POS);
    }

}
