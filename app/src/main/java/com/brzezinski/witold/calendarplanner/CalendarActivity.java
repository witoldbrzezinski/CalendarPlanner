package com.brzezinski.witold.calendarplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;

import com.brzezinski.witold.calendarplanner.database.DatabaseManager;
import com.brzezinski.witold.calendarplanner.model.Event;
import com.brzezinski.witold.calendarplanner.utils.DateUtils;
import com.brzezinski.witold.calendarplanner.utils.EventListAdapter;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.brzezinski.witold.calendarplanner.EventActivity.DASH;


public class CalendarActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    @BindView(R.id.calendarRecyclerView)
    RecyclerView calendarRecyclerView;
    @BindView(R.id.addEventButton)
    FloatingActionButton addEventButton;
    @BindView(R.id.calendarView)
    CalendarView calendarView;

    private DatabaseManager databaseManager = DatabaseManager.getDatabaseManager();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private DateUtils dateUtils = new DateUtils();

    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String DAY = "DAY";
    public static final String DATE_INTENT = "DATE_INTENT";
    public static final String INTENT_NAME = "NAME";

    Context mContext;
    int mDay;
    int mMonth;
    int mYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        getEvents(dateUtils.getCurrentDate());
        getCurrentDateParams();
    }


    private void loadActivity(){
        setContentView(R.layout.calendar_layout);
        ButterKnife.bind(this);
        mContext = getBaseContext();
        calendarView.setOnDateChangeListener(CalendarActivity.this);
        setListeners();
    }

    private void getCurrentDateParams(){
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.showEventsMenuItem:
                setEventsListActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setListeners(){
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectParamsAndStartEventActivity();
            }
        });
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
        mDay = day;
        mMonth = month+1;
        mYear = year;
        getEvents(parseDateToString());
    }

    private void collectParamsAndStartEventActivity(){
        Intent dateIntent = new Intent(this,EventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_NAME,DATE_INTENT);
        bundle.putInt(YEAR,mYear);
        bundle.putInt(MONTH,mMonth);
        bundle.putInt(DAY,mDay);
        dateIntent.putExtras(bundle);
        startActivity(dateIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        quitApp();
    }

    public void quitApp(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setRecyclerView(List<Event> list){
        mContext = this.getApplicationContext();
        mLayoutManager = new LinearLayoutManager(mContext);
        calendarRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new EventListAdapter(list);
        calendarRecyclerView.setAdapter(adapter);
    }

    private void getEvents(String date){
        List<Event> eventList = databaseManager.getEventFromDate(date);
        setRecyclerView(eventList);
    }

    private String parseDateToString(){
        return String.valueOf(mYear)+DASH+dateUtils.parseDateParamToString(mMonth)+DASH+dateUtils.parseDateParamToString(mDay);
    }

    public void setEventsListActivity(){
        Intent eventsListActivity = new Intent(this,EventListActivity.class);
        startActivity(eventsListActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEvents(parseDateToString());
    }

}
