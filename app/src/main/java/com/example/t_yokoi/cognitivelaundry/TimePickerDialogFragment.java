package com.example.t_yokoi.cognitivelaundry;

/**
 * Created by t-yokoi on 2016/07/11.
 */

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public int setHour;
    public int setMinute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        setHour = c.get(Calendar.HOUR_OF_DAY);
        setMinute = c.get(Calendar.MINUTE);
        int hour = MainActivity.pre.getInt("hour", setHour);
        int minute = MainActivity.pre.getInt("minute", setMinute);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //時刻が選択されたときの処理
        setHour = hourOfDay;
        setMinute = minute;
        Toast.makeText(getContext(), setHour + ":" + minute, Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor = MainActivity.pre.edit();
        editor.putInt("hour", setHour);
        editor.putInt("minute", setMinute);
        editor.apply();
    }
}