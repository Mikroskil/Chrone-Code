package com.mikroskil.android.qattend;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mikroskil.android.qattend.db.Contract;
import com.mikroskil.android.qattend.db.model.ParseEvent;
import com.mikroskil.android.qattend.db.model.ParseOrganization;
import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ManageEventActivity extends Activity {

    public static final String ARG_PICKER_ID = "picker_id";
    public static final String ARG_PICKER_HOUR = "picker_hour";
    public static final String ARG_PICKER_MINUTE = "picker_minute";
    public static final String ARG_PICKER_YEAR = "picker_year";
    public static final String ARG_PICKER_MONTH = "picker_month";
    public static final String ARG_PICKER_DAY = "picker_day";

    public static final int START_PICKER_ID = 1;
    public static final int END_PICKER_ID = 2;

    protected Button mStartDateBtn;
    protected Button mStartTimeBtn;
    protected Button mEndDateBtn;
    protected Button mEndTimeBtn;
    protected SimpleDateFormat mDateFormat = new SimpleDateFormat(Contract.DATE_FORMAT);
    protected SimpleDateFormat mTimeFormat = new SimpleDateFormat(Contract.TIME_FORMAT);
    protected SimpleDateFormat mDateTimeFormat = new SimpleDateFormat(Contract.DATE_TIME_FORMAT);

    private Uri currentUri;
    private boolean isNew;
    private TextView mTitleView;
    private TextView mLocationView;
    private TextView mDescView;
    private CheckBox mPrivacyView;

    private String mTitle;
    private String mLocation;
    private String mEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNew = getIntent().getBooleanExtra(MainActivity.EVENT_MODE, true);
        final ActionBar actionBar = getActionBar();
        final LayoutInflater inflater = (LayoutInflater) actionBar
                .getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View actionBarView = inflater.inflate(R.layout.action_bar_dialog, null);
        actionBarView.findViewById(R.id.action_bar_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateEvent();
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

        mTitleView = (TextView) findViewById(R.id.title);
        mLocationView = (TextView) findViewById(R.id.location);
        mDescView = (TextView) findViewById(R.id.desc);
        mPrivacyView = (CheckBox) findViewById(R.id.privacy);
        mStartDateBtn = (Button) findViewById(R.id.startDate);
        mStartTimeBtn = (Button) findViewById(R.id.startTime);
        mEndDateBtn = (Button) findViewById(R.id.endDate);
        mEndTimeBtn = (Button) findViewById(R.id.endTime);

        final Calendar cal = Calendar.getInstance();
        if (isNew) {
            mStartDateBtn.setText(mDateFormat.format(cal.getTime()));
            mStartTimeBtn.setText(mTimeFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);
            mEndDateBtn.setText(mDateFormat.format(cal.getTime()));
            mEndTimeBtn.setText(mTimeFormat.format(cal.getTime()));
        } else {
            currentUri = Uri.parse(Contract.Event.CONTENT_URI + "/" + getIntent().getLongExtra(Contract.Event._ID, 0));
            Cursor cursor = getContentResolver().query(currentUri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    ParseEvent.Event event = ParseEvent.fromCursor(cursor);
                    mTitleView.setText(event.title);
                    mLocationView.setText(event.location);
                    mStartDateBtn.setText(mDateFormat.format(event.startDate));
                    mStartTimeBtn.setText(mTimeFormat.format(event.startDate));
                    mEndDateBtn.setText(mDateFormat.format(event.endDate));
                    mEndTimeBtn.setText(mTimeFormat.format(event.endDate));
                    mPrivacyView.setChecked(event.privacy);
                    mDescView.setText(event.desc);
                    mEventId = event.objId;
                }
                cursor.close();
            }
        }

        mStartDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cal.setTime(mDateFormat.parse(mStartDateBtn.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DialogFragment datePicker = DatePickerFragment.newInstance(START_PICKER_ID,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        mStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cal.setTime(mTimeFormat.parse(mStartTimeBtn.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DialogFragment timePicker = TimePickerFragment.newInstance(START_PICKER_ID,
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                timePicker.show(getFragmentManager(), "timePicker");
            }
        });

        mEndDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cal.setTime(mDateFormat.parse(mEndDateBtn.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DialogFragment datePicker = DatePickerFragment.newInstance(END_PICKER_ID,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        mEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cal.setTime(mTimeFormat.parse(mEndTimeBtn.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DialogFragment timePicker = TimePickerFragment.newInstance(END_PICKER_ID,
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                timePicker.show(getFragmentManager(), "timePicker");
            }
        });

    }

    private void attemptCreateEvent() {
        Log.d(QattendApp.TAG, "attempt create event");

        mTitleView.setError(null);
        mLocationView.setError(null);

        mTitle = mTitleView.getText().toString().trim();
        mLocation = mLocationView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mLocation)) {
            mLocationView.setError(getString(R.string.error_field_required));
            focusView = mLocationView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mTitle)) {
            mTitleView.setError(getString(R.string.error_field_required));
            focusView = mTitleView;
            cancel = true;
        }

        if (cancel) focusView.requestFocus();
        else if (isNew) createEvent();
        else editEvent();
    }

    private void createEvent() {
        // TODO remove this block if sync already works
        ParseEvent event = new ParseEvent();
        event.setTitle(mTitle);
        try {
            event.setStartDate(mDateTimeFormat.parse(mStartDateBtn.getText() + " " + mStartTimeBtn.getText() + ":00"));
            event.setEndDate(mDateTimeFormat.parse(mEndDateBtn.getText() + " " + mEndTimeBtn.getText() + ":00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event.setLocation(mLocation);
        event.setDesc(mDescView.getText().toString().trim());
        event.setPrivacy(mPrivacyView.isChecked());
        event.initTicketCount();
        ParseOrganization org = ParseObject.createWithoutData(ParseOrganization.class, NavigationDrawerFragment.getActiveOrgId());
        event.setHostBy(org);
        event.saveEventually();

        getContentResolver().insert(Contract.Event.CONTENT_URI, event.getContentValues());
        finish();
    }

    private void editEvent() {
        // TODO remove this block if sync already works
        ParseEvent event = ParseObject.createWithoutData(ParseEvent.class, mEventId);
        event.setTitle(mTitle);
        try {
            event.setStartDate(mDateTimeFormat.parse(mStartDateBtn.getText() + " " + mStartTimeBtn.getText() + ":00"));
            event.setEndDate(mDateTimeFormat.parse(mEndDateBtn.getText() + " " + mEndTimeBtn.getText() + ":00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event.setLocation(mLocation);
        event.setDesc(mDescView.getText().toString().trim());
        event.setPrivacy(mPrivacyView.isChecked());
        event.saveEventually();

        getContentResolver().update(currentUri, event.getContentValues(), null, null);
        finish();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private int mPickerId;

        public static DatePickerFragment newInstance(int id, int year, int month, int day) {
            Bundle args = new Bundle();
            args.putInt(ARG_PICKER_ID, id);
            args.putInt(ARG_PICKER_YEAR, year);
            args.putInt(ARG_PICKER_MONTH, month);
            args.putInt(ARG_PICKER_DAY, day);
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = getArguments().getInt(ARG_PICKER_YEAR);
            int month = getArguments().getInt(ARG_PICKER_MONTH);
            int day = getArguments().getInt(ARG_PICKER_DAY);
            mPickerId = getArguments().getInt(ARG_PICKER_ID);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ManageEventActivity parent = (ManageEventActivity) getActivity();
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);

            switch (mPickerId) {
                case START_PICKER_ID:
                    parent.mStartDateBtn.setText(parent.mDateFormat.format(cal.getTime()));
                    break;
                case END_PICKER_ID:
                    parent.mEndDateBtn.setText(parent.mDateFormat.format(cal.getTime()));
                    break;
            }
        }

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private int mPickerId;

        public static TimePickerFragment newInstance(int id, int hour, int minute) {
            Bundle args = new Bundle();
            args.putInt(ARG_PICKER_ID, id);
            args.putInt(ARG_PICKER_HOUR, hour);
            args.putInt(ARG_PICKER_MINUTE, minute);
            TimePickerFragment fragment = new TimePickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour = getArguments().getInt(ARG_PICKER_HOUR);
            int minute = getArguments().getInt(ARG_PICKER_MINUTE);
            mPickerId = getArguments().getInt(ARG_PICKER_ID);
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ManageEventActivity parent = (ManageEventActivity) getActivity();
            Calendar cal = Calendar.getInstance();
            cal.set(0, 0, 0, hourOfDay, minute, 0);

            switch (mPickerId) {
                case START_PICKER_ID:
                    parent.mStartTimeBtn.setText(parent.mTimeFormat.format(cal.getTime()));
                    break;
                case END_PICKER_ID:
                    parent.mEndTimeBtn.setText(parent.mTimeFormat.format(cal.getTime()));
                    break;
            }
        }
    }

}
