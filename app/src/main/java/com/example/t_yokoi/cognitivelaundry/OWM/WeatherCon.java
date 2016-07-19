package com.example.t_yokoi.cognitivelaundry.OWM;

import android.graphics.Bitmap;
import org.json.JSONArray;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by masa on 2016/07/13.
 */
public class WeatherCon {
    final String country = "jp";
    final String countryTime = "JST";
    final int nDay = 4;

    // request data
    private Bitmap[] weatherIcon = new Bitmap[nDay];
    private double[] amountRain = new double[nDay];

    // return 1  : ok
    // return 0 : don't need call
    // return -1 : can't get weather data
    public int setWeatherData(String cityname){
        OWMAct oa = new OWMAct();
        OpenWeatherMap owm = new OpenWeatherMap();

        // データをurlコールする必要がない
        if(owm.isCallBeforein3hours() == false && owm.getBeforeCityName() == cityname) return 0;

        String rootStr = owm.getWeatherJSON_cityname(cityname, country);
        JSONArray rootArr = owm.parseJSONdata(rootStr);

        // cnt==0ならデータなしなのでエラー
        if(owm.isNotExistWeatherdata())  return -1;

        // 各日付の6時のUNIXtimeを取得
        int firstutime = owm.getFirstUtime();
        Calendar cal = Calendar.getInstance();
        TimeZone area = TimeZone.getTimeZone(countryTime);
        cal.setTimeInMillis( (long)(firstutime*1000) );
        cal.setTimeZone(area);
        int firsthour = cal.get(Calendar.HOUR_OF_DAY);
        // 初期時刻が18時以降なら明日から取得
        if(firsthour>18){
            cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE + 1, 6, 0);
        }else{
            cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE, 6, 0);
        }

        // 降雨量取得
        int utime6 = (int)(cal.getTimeInMillis()/1000L);
        for (int i=0; i<nDay ;i++){
            amountRain[i] = oa.getAmountRain6to18(rootArr, utime6 + i*86400); // 一日ずつずれる
        }

        // 画像取得
        OWMBmp ob = new OWMBmp();
        String[] iconid = new String[nDay];
        for (int i=0; i<nDay; i++){
            iconid[i]=oa.getWeatherDayicon(rootArr, utime6 + i*86400);
            weatherIcon[i] = ob.getIconBitmap(iconid[i]);
        }

        return 1;
    }

    // 雨量参照
    // 0 <= th_day <= (nDay-1)
    public double getAmountRain(int th_day){
        return amountRain[th_day];
    }

    // 画像参照
    // 0<=th_day<=(nDay-1)
    public Bitmap getBitmap(int th_day){
        return weatherIcon[th_day];
    }
}
