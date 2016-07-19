package com.example.t_yokoi.cognitivelaundry.OWM;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by masa on 2016/07/11.
 */
public class OWMAct {

    private OpenWeatherMap owm = new OpenWeatherMap();
    final int utime3hours = 10800;
    final int count3hours = 3;

    static int cloudThreshold=40;

    // return amount of rain at 6 to 18 time
    // It almost don't consider the lack of data
    // utime6 is the unix time at 6 on the day wanted
    public double getAmountRain6to18(JSONArray listArray, int utime6){
        double amount=0.0;
        JSONObject obj;
        for(int i=utime6 ; i<=utime6+count3hours*utime3hours; i+=utime3hours) {
            obj = owm.getWeatherObjUtime(listArray, i);
            amount += owm.getAmountRain(obj);
        }
        return amount;
    }

    // 晴天の数
    public int getCountClear6to18(JSONArray listArray, int utime6){
        int count=0;
        JSONObject obj;
        for(int i=utime6 ; i<=utime6+count3hours*utime3hours; i+=utime3hours) {
            obj = owm.getWeatherObjUtime(listArray, i);
            if(owm.getWeatherString(obj) == "Clear") count++;
        }
        return count;
    }

    // 空全体の雲の割合が特定割り以下である数
    public int getCountCloudU50per6to18(JSONArray listArray, int utime6){
        int count=0;
        JSONObject obj;
        for(int i=utime6 ; i<=utime6+count3hours*utime3hours; i+=utime3hours) {
            obj = owm.getWeatherObjUtime(listArray,i);
            if(owm.getCloudPercentage(obj)<=cloudThreshold) count++;
        }
        return count;
    }

    // 6to18の一日のicon
    // 晴れと雨が同じ回数などを考慮しない
    public String getWeatherDayicon(JSONArray listArray, int utime6){
        int[] count = new int[5];
        String[] icon = new String[5];
        String tmp;
        int c1=0; // iconの現在の配列数
        int c2=0; // 今参照している配列の添え字
        JSONObject obj;
        int j;
        obj = owm.getWeatherObjUtime(listArray,utime6);
        icon[c2] = owm.getWeatherIconId(obj);
        c1++;
        int i;
        for(i=utime6+utime3hours ; i<=utime6+count3hours*utime3hours; i+=utime3hours) {
            obj = owm.getWeatherObjUtime(listArray,i);
            tmp=owm.getWeatherIconId(obj);
            c2=0;
            while(icon[c2]!=tmp) {
                c2++;
                if (c2 == c1) {
                    icon[c2]=tmp;
                    c1++;
                    break;
                }
            }
            count[c2]++;
        }
        int max, imax, temp;
        max=0;
        imax=0;
        for(i=0; i<c1; i++){
            temp=count[i];
            if(temp>max){
                max=temp;
                imax=i;
            }
        }
        return icon[imax];
    }


}
