package com.mikroskil.android.qattend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.mikroskil.android.qattend.db.QattendDatabase;
import com.parse.ParseUser;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (SettingsFragment.class.getName().equals(fragmentName))
            return true;
        else
            return false;
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            // PreferenceManager.setDefaultValues(getActivity(),
            //         R.xml.advanced_preferences, false);

            // Can retrieve arguments from preference XML.
            Log.d(QattendApp.TAG, "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences);
        }

        @Override
        public boolean onPreferenceTreeClick (PreferenceScreen prefScreen, Preference pref) {
            if(getString(R.string.change_password_key).equals(pref.getKey())) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
            else if (getString(R.string.sign_out_key).equals(pref.getKey())) {
                new LogoutDialogFragment().show(getFragmentManager(), "LogoutDialogFragment");
            }

            return false;
        }

        public static class LogoutDialogFragment extends DialogFragment {

            public LogoutDialogFragment() {}

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.dialog_sign_out)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                if (sp.edit().clear().commit()) {
                                    QattendDatabase dbHelper = new QattendDatabase(getActivity());
                                    final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    dbHelper.onUpgrade(db, QattendDatabase.DB_VERSION, QattendDatabase.DB_VERSION + 1);
                                    ParseUser.logOut();
                                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(getActivity(), "Failed to clear your preferences.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            }
        }
    }

}
