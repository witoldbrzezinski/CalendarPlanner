package com.brzezinski.witold.calendarplanner;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.brzezinski.witold.calendarplanner.database.DatabaseManager;
import com.brzezinski.witold.calendarplanner.model.Event;
import com.brzezinski.witold.calendarplanner.utils.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

import static com.brzezinski.witold.calendarplanner.CalendarActivity.DAY;
import static com.brzezinski.witold.calendarplanner.CalendarActivity.MONTH;
import static com.brzezinski.witold.calendarplanner.CalendarActivity.YEAR;


public class EventActivity extends AppCompatActivity {

    public static final String DASH = "-";
    String name = "";
    String startDate = "";
    String endDate = "";
    String startHour = "";
    String endHour = "";
    String description = "";
    String place = "";
    int hourFromStart,minuteFromStart,hoursBefore,minutesBefore;
    boolean placeCheck = false;
    boolean nameCheck = false;
    int startYear, startMonth, startDay;
    int endYear, endMonth, endDay;
    int hourFromEnd, minutesFromEnd;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
    private DatabaseManager databaseManager = DatabaseManager.getDatabaseManager();
    private DateUtils dateUtils = new DateUtils();
    private Date firstDate = new Date();
    private Date secondDate = new Date();

    @BindView(R.id.reminderSpinner)
    Spinner reminderSpinner;
    @BindView(R.id.eventNameEditText)
    EditText nameEditText;
    @BindView(R.id.startDayValueTextView)
    TextView startDayTextView;
    @BindView(R.id.endDayEditText)
    EditText endDayEditText;
    @BindView(R.id.startHourEditText)
    EditText startHourEditText;
    @BindView(R.id.endHourEditText)
    EditText endHourEditText;
    @BindView(R.id.descriptionEditText)
    EditText descriptionEditText;
    @BindView(R.id.saveButton)
    Button saveButton;
    @BindView(R.id.placeEditText)
    EditText placeEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_layout);
        ButterKnife.bind(this);
        collectParams();
        setListeners();
        reminderSpinner.setSelection(2);
        requestPermissionsIfNeeded();
    }

    private void setStartDate() {
        if (getIntentData(YEAR) != 0) {
            startDayTextView.setText(startYear + DASH + dateUtils.parseDateParamToString(startMonth)
                    + DASH + dateUtils.parseDateParamToString(startDay));
        } else {
            startDayTextView.setText(dateUtils.getCurrentDate());
        }
    }

    private int getIntentData(String intentName) {
        Bundle bundle = getIntent().getExtras();
        int value = bundle.getInt(intentName);
        return value;
    }

    private void collectParams() {
        startYear = getIntentData(YEAR);
        startMonth = getIntentData(MONTH);
        startDay = getIntentData(DAY);
        setStartDate();
    }

    private void setDateOnEditText(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(this, dateListener, year, month, day);
        datePicker.show();
    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            endDayEditText.setText(year + DASH + dateUtils.parseDateParamToString(month + 1) + DASH +
                    dateUtils.parseDateParamToString(day));
            endYear = year;
            endMonth = month+1;
            endDay = day;
        }
    };

    private void setHourOnEditText(final EditText editText) {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                editText.setText(selectedHour + ":" + dateUtils.parseDateParamToString(selectedMinute));
                if (editText == startHourEditText) {
                    hourFromStart = selectedHour;
                    minuteFromStart = selectedMinute;
                }
                if (editText==endHourEditText){
                    hourFromEnd = selectedHour;
                    minutesFromEnd = selectedMinute;
                }
            }
        }, hour, minute, true);
        mTimePicker.show();
    }

    private void getEventDetails() {
        name = nameEditText.getText().toString();
        startDate = startDayTextView.getText().toString();
        endDate = endDayEditText.getText().toString();
        startHour = startHourEditText.getText().toString();
        endHour = endHourEditText.getText().toString();
        description = descriptionEditText.getText().toString();
        place = placeEditText.getText().toString();
    }

    private Event getEvent() throws ParseException {
        Event event = new Event(name, startDate, endDate, startHour, endHour, description, place);
        return event;
    }

    private void setListeners() {
        startHourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHourOnEditText(startHourEditText);
            }
        });
        endHourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHourOnEditText(endHourEditText);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWrongTimeMessageIfNeeded();
                showEmptyParameterDialogIfNeeded();
                getEventDetails();
                if ((compareDates() < 0 && !nameCheck && !placeCheck) || (compareDates() == 0 && !isTimeCorrect() && !nameCheck && !placeCheck)) {
                    try {
                        databaseManager.insertEvent(getEvent());
                        addReminder();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_LONG).show();
                }
            }
        });
        endDayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateOnEditText(endDayEditText);
            }
        });
    }

    private void showWrongTimeMessageIfNeeded() {
        if (compareDates() > 0 || (endDayEditText.getText().toString().equals(""))
                ) {
            showDateErrorDialogIfNeeded(getString(R.string.wrongDay));
        } else if (compareDates() == 0 && isTimeCorrect() || (startHourEditText.getText().toString().equals("")) || (endHourEditText.getText().toString().equals(""))) {
            showDateErrorDialogIfNeeded(getString(R.string.wrongHour));
        }
    }

    private boolean isTimeCorrect() {
        if (dateUtils.validateTime(startHourEditText.getText().toString(), endHourEditText.getText().toString()))
            return true;
        else
            return false;
    }

    private void showDateErrorDialogIfNeeded(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int compareDates() {
        int value;
        try {
            firstDate = dateUtils.parseStringToDateDay(startDayTextView.getText().toString());
            secondDate = dateUtils.parseStringToDateDay(endDayEditText.getText().toString());
            value = firstDate.compareTo(secondDate);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showEmptyParameterDialogIfNeeded() {
        placeCheck = (placeEditText.getText().toString()).equals("");
        nameCheck = (nameEditText.getText().toString()).equals("");
        if (placeCheck && nameCheck) {
            showDateErrorDialogIfNeeded(getString(R.string.fillNameAndPlace));
        }
        if (placeCheck && !nameCheck) {
            showDateErrorDialogIfNeeded(getString(R.string.fillPlace));
        }
        if (!placeCheck && nameCheck) {
            showDateErrorDialogIfNeeded(getString(R.string.fillName));
        }
    }


    private Date getStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(startYear, startMonth - 1, startDay, hourFromStart, minuteFromStart , 0);
        Date date = calendar.getTime();
        return date;
    }

    private Date getEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(endYear, endMonth - 1, endDay, hourFromEnd, minutesFromEnd , 0);
        Date date = calendar.getTime();
        return date;
    }

    @OnItemSelected({R.id.reminderSpinner})
    void onItemSelected(Spinner spinner, int position) {
        switch (position) {
            case 0:
                setReminderParams(0, 15);
                break;
            case 1:
                setReminderParams(0, 30);
                break;
            case 2:
                setReminderParams(1, 0);
                break;
            case 3:
                setReminderParams(2, 0);
                break;
            case 4:
                setReminderParams(3, 0);
                break;
            case 5:
                setReminderParams(6, 0);
                break;
        }
    }

    private void setReminderParams(int hours, int minutes) {
        hoursBefore = hours;
        minutesBefore = minutes;
    }

    public void requestPermissionsIfNeeded(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)){
                requestPermissions(PERMISSIONS,PERMISSION_ALL);}
        }
    }

    public boolean hasPermissions(String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCalendarUriBase(Activity act) {
        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }

    private void addReminder(){
        int minutesToReminder = minutesBefore+60*hoursBefore;
        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(this) + "events");
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put("title", name);
        values.put("allDay", 0);
        values.put("dtstart", getStartDate().getTime());
        values.put("dtend", getEndDate().getTime());
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put("hasAlarm", 1);
        Uri event = cr.insert(EVENTS_URI, values);

        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(this) + "reminders");
        values = new ContentValues();
        values.put( "event_id", Long.parseLong(event.getLastPathSegment()));
        values.put( "method", 1 );
        values.put( "minutes", minutesToReminder );
        cr.insert( REMINDERS_URI, values );
    }

}
