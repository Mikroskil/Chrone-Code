package com.mikroskil.android.qattend.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mikroskil.android.qattend.CardSimpleAdapter;
import com.mikroskil.android.qattend.JSONParser;
import com.mikroskil.android.qattend.MainActivity;
import com.mikroskil.android.qattend.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberFragment extends Fragment {

    private static String URL = "http://qattend.herokuapp.com/sites/process.php";
//    private static final String URL = "http://10.0.2.2/web/sites/process.php";
    private static final String HOST = "http://qattend.herokuapp.com/";
//    private static final String HOST = "http://10.0.2.2/web/";
    private static final String TAG_MEMBERS = "members";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHOTO = "photo";
    private static ArrayList<HashMap<String, String>> memberList = new ArrayList<HashMap<String, String>>();
    private int position;

    private static JSONParser jsonParser;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public MemberFragment(int position) {
        this.position = position;
        jsonParser = new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_member, container, false);
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
            return jsonParser.getJSONFromUrl(URL, TAG_MEMBERS);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            JSONArray events = null;

            try {
                events = json.getJSONArray(TAG_MEMBERS);

                for (int i = 0; i < events.length(); i++) {
                    JSONObject m = events.getJSONObject(i);
                    String photo = HOST + m.getString(TAG_PHOTO);
                    String title = m.getString(TAG_NAME);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_PHOTO, photo);
                    map.put(TAG_NAME, title);

                    memberList.add(map);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            CardSimpleAdapter adapter = new CardSimpleAdapter(getActivity(), memberList,
                    android.R.layout.simple_list_item_1, new String[] { TAG_NAME },
                    new int[] { android.R.id.text1 });
            listView.setAdapter(adapter);
        }
    }
}