package com.brzezinski.witold.calendarplanner.database;

import com.brzezinski.witold.calendarplanner.model.Event;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DatabaseManager {

    private Realm mRealm;
    private static DatabaseManager databaseManager;

    public DatabaseManager() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        mRealm = Realm.getInstance(realmConfiguration);
    }

    public static DatabaseManager getDatabaseManager(){
        if(databaseManager==null){
            databaseManager = new DatabaseManager();
        }
        return databaseManager;
    }

    public void insertEvent(Event event){
        mRealm.beginTransaction();
        mRealm.insertOrUpdate(event);
        mRealm.commitTransaction();
    }

    public List<Event> getAllEvents(){
        RealmResults<Event> eventRealmResults = mRealm.where(Event.class).findAll();
        return  eventRealmResults;
    }

    public List<Event> getEventFromDate(String date){
        RealmResults<Event> eventRealmResults = mRealm.where(Event.class).equalTo("startDate",date).findAll();
        return  eventRealmResults;
    }

    public void deleteEvent (final Event event){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                event.deleteFromRealm();
            }
        });
    }

}
