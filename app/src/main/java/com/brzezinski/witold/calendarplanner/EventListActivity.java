package com.brzezinski.witold.calendarplanner;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.brzezinski.witold.calendarplanner.database.DatabaseManager;
import com.brzezinski.witold.calendarplanner.model.Event;
import com.brzezinski.witold.calendarplanner.utils.EventListAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventListActivity extends AppCompatActivity {

    @BindView(R.id.eventListRecyclerView)
    RecyclerView eventsRecyclerView;

    private DatabaseManager databaseManager = DatabaseManager.getDatabaseManager();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    Context mContext;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list_layout);
        ButterKnife.bind(this);
        getEvents();
    }

    private void setRecyclerView(List<Event> list){
        mContext = this.getApplicationContext();
        mLayoutManager = new LinearLayoutManager(mContext);
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new EventListAdapter(list);
        eventsRecyclerView.setAdapter(adapter);
    }

    private void getEvents(){
        List<Event> eventList = databaseManager.getAllEvents();
        setRecyclerView(eventList);
    }

}
