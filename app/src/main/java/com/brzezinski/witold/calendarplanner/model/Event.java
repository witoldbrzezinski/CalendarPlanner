package com.brzezinski.witold.calendarplanner.model;

import io.realm.RealmObject;

public class Event extends RealmObject {

    String name;
    String startDate;
    String endDate;
    String startHour;
    String endHour;
    String description;
    String place;

    public Event(String name, String startDate,String endDate ,String startHour, String endHour, String description, String place) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startHour = startHour;
        this.endHour = endHour;
        this.description = description;
        this.place = place;
    }

    public Event(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEndDate() {
        return endDate;
    }

}
