package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mikroskil.android.qattend.CardSimpleAdapter;
import com.mikroskil.android.qattend.DetailActivity;
import com.mikroskil.android.qattend.JSONParser;
import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EventFragment extends Fragment {

    private static String URL = "http://qattend.herokuapp.com/sites/process.php";
//    private static final String URL = "http://10.0.2.2/web/sites/process.php";
    private static final String HOST = "http://qattend.herokuapp.com/";
//    private static final String HOST = "http://10.0.2.2/web/";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PHOTO = "photo";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_END_TIME = "end_time";
    private static final String TAG_TIME = "time";
    private static ArrayList<HashMap<String, String>> eventList = new ArrayList<HashMap<String, String>>();
    private static View space;
    private int position;

    private static JSONParser jsonParser;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public EventFragment(int position) {
        this.position = position;
        jsonParser = new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
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
                    String photo = HOST + m.getString(TAG_PHOTO);
                    String title = m.getString(TAG_TITLE);
                    String location = m.getString(TAG_LOCATION);
                    String time = m.getString(TAG_START_TIME);
                    String tmp = m.getString(TAG_END_TIME);
                    if (!tmp.isEmpty()) time += " - " + tmp;

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_PHOTO, photo);
                    map.put(TAG_TITLE, title);
                    map.put(TAG_LOCATION, location);
                    map.put(TAG_TIME, time);

                    eventList.add(map);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            CardSimpleAdapter adapter = new CardSimpleAdapter(getActivity(), eventList,
                    R.layout.card, new String[] { TAG_TITLE, TAG_LOCATION, TAG_TIME },
                    new int[] { R.id.title, R.id.location, R.id.time });
            space = new View(getActivity());
            listView.addFooterView(space, null, false);
            listView.addHeaderView(space, null, false);
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
