package com.mikroskil.android.qattend;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static String URL = "http://qattend.herokuapp.com/web/sites/process.php";
//    private static final String URL = "http://10.0.2.2/web/sites/process.php";
    private static final String HOST = "http://qattend.herokuapp.com/";
//    private static final String HOST = "http://10.0.2.2/";
    private static final String TAG_MEMBERS = "members";
    private static final String TAG_NAME = "name";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PHOTO = "photo";
    private static ArrayList<HashMap<String, String>> memberList = new ArrayList<HashMap<String, String>>();
    private static ArrayList<HashMap<String, String>> eventList = new ArrayList<HashMap<String, String>>();

    private static JSONParser jsonParser;

    private String[] sectionMenus;

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
        setContentView(R.layout.activity_main);

        jsonParser = new JSONParser();

        sectionMenus = getResources().getStringArray(R.array.section_menus);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        mTitle = sectionMenus[number - 1];
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
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_create_event:
                Toast.makeText(this, R.string.action_create_event, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            new FetchJSON(listView).execute();
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        private class FetchJSON extends AsyncTask<String, String, JSONObject> {

            private ProgressDialog pDialog;
            private ListView listView;

            public FetchJSON(ListView listView) {
                this.listView = listView;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Getting Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... strings) {
                return jsonParser.getJSONFromUrl(URL, TAG_EVENTS);
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                pDialog.dismiss();
                JSONArray events = null;

                try {
                    events = json.getJSONArray(TAG_EVENTS);

                    for (int i = 0; i < events.length(); i++) {
                        JSONObject m = events.getJSONObject(i);
                        String title = m.getString(TAG_TITLE);
                        // String photo = HOST + m.getString(TAG_PHOTO);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_TITLE, title);
                        // map.put(TAG_PHOTO, photo);

                        eventList.add(map);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                CardSimpleAdapter adapter = new CardSimpleAdapter(getActivity(), eventList,
                        R.layout.card, new String[] { TAG_TITLE }, new int[] { R.id.title });
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d("LIST", "list"+i);
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("LIST_INDEX", i);
                        startActivity(intent);
                    }
                });
                // listView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            }
        }
    }
}
