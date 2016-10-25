package com.example.phompang.eatyfinder.model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by phompang on 10/25/2016 AD.
 */

public class Datetime {
    private String date = "";
    private String time = "";

    public ArrayList<String> date_data;
    public ArrayList<String> time_data;

    public Datetime() {
        initDateArray();
        initTimeArray();
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void pickTime(int position) {
        switch (position) {
            case 0:
                this.time = "09:00:00";
                break;
            case 1:
                this.time = "13:00:00";
                break;
            case 2:
                this.time = "17:00:00";
                break;
            case 3:
                this.time = "20:00:00";
                break;
            default:
                break;
        }
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void pickDate(int position) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DATE);
        switch (position) {
            case 0:
                this.date = Integer.toString(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", date);
                break;
            case 1:
                c.add(Calendar.DAY_OF_YEAR, 1);
                date = c.get(Calendar.DATE);
                this.date = Integer.toString(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", date);
                break;
            case 2:
                int i = c.get(Calendar.WEEK_OF_MONTH);
                c.set(Calendar.WEEK_OF_MONTH, ++i);
                date = c.get(Calendar.DATE);
                this.date = Integer.toString(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", date);
            default:
                break;
        }
    }

    public String getDatetime() {
        return this.date + " " + this.time;
    }

    private void initDateArray() {
        date_data = new ArrayList<>();
        date_data.add("Today");
        date_data.add("Tomorrow");
        date_data.add("Next week");
        date_data.add("Pick a date...");
    }

    private void initTimeArray() {
        time_data = new ArrayList<>();
        time_data.add("Morning (09:00)");
        time_data.add("Afternoon (13:00)");
        time_data.add("Evening (17:00)");
        time_data.add("Night (20:00)");
        time_data.add("Pick a time...");
    }
}
