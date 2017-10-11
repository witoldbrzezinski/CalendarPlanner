package com.brzezinski.witold.calendarplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCalendarActivity();
    }

    public void setCalendarActivity(){
        Intent calendarIntent = new Intent(this,CalendarActivity.class);
        startActivity(calendarIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setCalendarActivity();
    }

}
