package com.example.t_yokoi.cognitivelaundry;

import android.content.SharedPreferences;

/**
 * Created by t-yokoi on 2016/07/18.
 */
public class UserData {
    public int hour;
    public int minute;
    public String place;
    public int num;
    public int firstDayNum;

    public UserData(SharedPreferences preferences) {
        hour = preferences.getInt("hour", 0);
        minute = preferences.getInt("minute", 0);
        place = preferences.getString("place", "shinjuku");
        num = preferences.getInt("num", 5);
        firstDayNum = preferences.getInt("first", num);
    }
}
