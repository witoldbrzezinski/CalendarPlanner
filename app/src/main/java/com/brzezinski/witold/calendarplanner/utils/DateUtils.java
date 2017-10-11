package com.brzezinski.witold.calendarplanner.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {

    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTimeString = df.format(calendar.getTime());
        return currentDateTimeString;
    }

    public boolean isAddingZeroNeeded(int value){
        if (value<10)
            return true;
        else return false;
    }

    public String parseDateParamToString(int integer) {
        if (isAddingZeroNeeded(integer)){
            return  "0"+Integer.toString(integer);
        } else
            return Integer.toString(integer);
    }

    public boolean validateTime(String time1, String time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        boolean b = false;
        try {
            java.util.Date startTime = sdf.parse(time1);
            java.util.Date endTime = sdf.parse(time2);
            b = endTime.before(startTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    public Date parseStringToDateDay(String stringDate) throws ParseException {
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.parse(stringDate);
        return date;
    }

    public String getDateStringForItem(String in){
        if (in!=null&&in.length()>=5){
            String out = in.substring(8,10)+"/"+in.substring(5,7);
            return out;}
        else return "";
    }

}
