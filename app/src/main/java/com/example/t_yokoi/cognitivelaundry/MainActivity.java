package com.example.t_yokoi.cognitivelaundry;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t_yokoi.cognitivelaundry.OWM.WeatherCon;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //    private String day_ = "day_";
    public static final int maxDays = 4;
    public static SharedPreferences pre;
    public static Day[] days = new Day[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pre = getSharedPreferences("user_data", MODE_PRIVATE);
        final UserData userData = new UserData(pre);
        init(userData);
//        WeatherCon wc = new WeatherCon();

        /**起動時の日時情報**/
        final Calendar calender = Calendar.getInstance();

        /**起動時の月日の文字列表記**/
        String date = getDateString(calender);

        Resources res = getResources();

        /**各日の情報を表示するためのView取得のためのid**/
        int dateId;
        int weatherId;
        int leftId;

        /**初日の枚数**/
        int firstDayNum = userData.firstDayNum;


         for (int i = 0; i < 5; i++) days[i] = new Day();

        /**データを保存するボタン**/
        Button save = (Button) findViewById(R.id.saveButton);
        if (save != null) {
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickSaveButton(userData);
                }
            });
        }

        /**入浴時刻(服の枚数が減る時間)ボタン**/
        Button bathTime = (Button) findViewById(R.id.bath_time);
        if (bathTime != null) {
            bathTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickBathButton();
                }
            });
        }


        /**日にち用TextViewの取得**/
        for (int i = 0; i < maxDays; i++) {
            /**月日の文字列表示**/
            dateId = res.getIdentifier("day_" + (i + 1), "id", getPackageName());
            days[i].date = (TextView) findViewById(dateId);
            if (days[i].date != null) {
                days[i].date.setText(date);
            }

            /**天候による画像の表示**/
            weatherId = res.getIdentifier("weather_" + (i + 1), "id", getPackageName());
            days[i].weather = (ImageView) findViewById(weatherId);
//            Bitmap bitmap = wc.getBitmap(i);
            if (days[i].weather != null) {
                // TODO: 2016/07/11 天候に応じた画像を表示させる
                days[i].weather.setImageResource(res.getIdentifier("sun", "mipmap", getPackageName()));
//                days[i].weather.setImageBitmap(bitmap);
            }

            /**洗濯のオススメ日かの表示**/

            /**残数の表示**/
            leftId = res.getIdentifier("left_" + (i + 1), "id", getPackageName());
            days[i].leftClothes = (TextView) findViewById(leftId);
            if (days[i].leftClothes != null) {
                int left;
                if (firstDayNum > i) left = firstDayNum - i;
                else left = 1;
                String s = String.valueOf(left);
                days[i].leftClothes.setText(s);
            }

            /**カレンダーの日にちを進める**/
            calender.add(Calendar.DAY_OF_MONTH, 1);
            date = getDateString(calender);

        }

    }

    /**
     * 画面の初期化
     **/
    private void init(UserData u) {
        EditText maxClothes = (EditText) findViewById(R.id.number_of_clothes);
        if (maxClothes != null) {
            String s = String.valueOf(u.num);
            maxClothes.setText(s);
            Log.v("init", "num:" + s);
        }

        EditText place = (EditText) findViewById(R.id.place);
        if (place != null) {
            place.setText(u.place);
            Log.v("init", "place:" + u.place);
        }

        EditText leftClothes = (EditText) findViewById(R.id.left_clothes);
        if (leftClothes != null) {
            String s = String.valueOf(u.firstDayNum);
            leftClothes.setText(s);
            Log.v("init", "first day num:" + s);
        }

    }

    /**
     * 保存ボタンを押したとき
     **/
    private void onClickSaveButton(UserData u) {
        EditText textPlace = (EditText) findViewById(R.id.place);
        if (textPlace != null) {
            u.place = textPlace.getText().toString();
            Log.v("saveButton", u.place);
        }
        EditText textNum = (EditText) findViewById(R.id.number_of_clothes);
        if (textNum != null) {
            String n = textNum.getText().toString();
            u.num = Integer.parseInt(n);
            Log.v("saveButton", u.num + "");
        }


        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("hour", u.hour);
        editor.putInt("minute", u.minute);
        editor.putString("place", u.place);
        editor.putInt("num", u.num);
//        editor.putInt("first", u.firstDayNum);
        editor.apply();

        AsyncHttpRequest task = new AsyncHttpRequest(this);
        task.execute(u.place);
//        Toast.makeText(this, "your data is saved.", Toast.LENGTH_LONG).show();
//        WeatherCon wc = new WeatherCon();
//        wc.setWeatherData(u.place);
//        for (int i = 0; i < 5; i++) {
//            Log.v("rain", "" + wc.getAmountRain(i));
//
//        }

    }

    /**
     * 入浴時刻入力のボタンを押したとき
     **/
    private void onClickBathButton() {
        TimePickerDialogFragment timePicker = new TimePickerDialogFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");

    }

    public String getDateString(Calendar cal) {
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        return month + "/" + date;
    }
}
