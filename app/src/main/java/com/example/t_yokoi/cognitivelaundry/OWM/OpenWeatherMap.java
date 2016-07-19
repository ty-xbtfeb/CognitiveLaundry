package com.example.t_yokoi.cognitivelaundry.OWM;
/**
 * Created by masa on 2016/07/08.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OpenWeatherMap {
    // need for requestURL
    final String basicURL = "http://api.openweathermap.org/data/2.5/forecast?";
    // final String basicURL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    final String apikey_number= "39f4f3cd560070db9b557c548e2550c7";
    final String apikey = "&APPID=" + apikey_number;
    // if you need to change request type, you change this. Now api return nearest requested position.
    String requesttype = "like";
    final String actualtype = "&type=" + requesttype;

    // the number of data list
    private static int count;
    // the first utime
    private static int firstutime=0;
    // the last utime
    private static int lastutime=0;
    // the position data
    private static String beforeCityName="NULL";

    // 以前のurlコールが3時間前か否か
    public boolean canrequest(int nowutime){
        return ( (nowutime-System.currentTimeMillis()/1000L)>=10800);
    }

    // get string JSON data to use cityname
    // exa. if you live in japan, country=jp
    // type 0:none 1:like 2:accurate
    public String getWeatherJSON_cityname(String cityname, String country){
        // q=Yokohama,jp
        String requestPosition = "q=" + cityname + "," +country;
        String requestURL = basicURL + requestPosition + actualtype + apikey;
        System.out.printf("%s\n",requestURL);

        String JSONdata=null;
        try {
            URL url = new URL(requestURL);
            URLConnection urlCon = url.openConnection();
            // urlCon.setRequestProperty("User-Agent","Mozilla/5.0");
            // urlCon.setDoInput(true);
            InputStream is = urlCon.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())){
                sb.append(line);
            }
            JSONdata = sb.toString();
        } catch (MalformedURLException e) {
            // miss url
            Log.e("TAG", "MURL");
            e.printStackTrace();
            System.err.println(e);
            JSONdata="-1";
        } catch (IOException e) {
            // miss data structer
            Log.e("TAG", "IOE");
            e.printStackTrace();
            System.err.println(e);
            JSONdata="-2";
        }
        beforeCityName = cityname;
        return JSONdata;
    }

    //String JSONdata parses
    //return JSONArray
    //if you use it, you also use existWeatherdata() after this.
    public JSONArray parseJSONdata(String JSONdata){
        JSONArray listArray;
        try {
            JSONObject rootObj = new JSONObject(JSONdata);
            count = rootObj.getInt("cnt");
            listArray = rootObj.getJSONArray("list");
        } catch (JSONException e) {
            // not exist exception data in JSONdata
            e.printStackTrace();
            listArray = null;
        }
        insertFirstUTime(listArray);
        insertLastUTime(listArray);
        return listArray;
    }

    // chech !cnt==0
    public boolean isNotExistWeatherData() {
        if (count==0) return true;
        else return false;
    }


    public boolean isCallBeforeIn3hours(){
        int nowutime = (int) (System.currentTimeMillis()/1000L);
        if( (nowutime - firstutime) <= 10800 ) return true;
        return false;
    }

    public String getBeforeCityName(){
        return beforeCityName;
    }

    public void insertFirstUTime(JSONArray listArray){
        try {
            JSONObject obj = listArray.getJSONObject(0);
            firstutime = obj.getInt("dt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getFirstUTime(){
        return firstutime;
    }

    public void insertLastUTime(JSONArray listArray){
        try{
            JSONObject obj = listArray.getJSONObject(count-1);
            lastutime = obj.getInt("dt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getLastUtime(){
        return lastutime;
    }

    //get the needed obj data in time
    //return obj
    //exa.UTC time = "2016-07-08 12:00:00"
    public JSONObject getWeatherObjStime(JSONArray listArray, String time){
        JSONObject obj=null;
        try {
            int i;
            for (i=0; i<count; i++) {
                obj = listArray.getJSONObject(i);
                if (obj.getString("dt_txt") == time) break;
            }
        } catch (JSONException e) {
            // not exist exception data in listArray
            e.printStackTrace();
            obj=null;
        }
        return obj;
    }

    //get the needed obj data in unix time
    //return obj. if the nearier data is not existed, return null
    // exa. time is unix time
    public JSONObject getWeatherObjUtime(JSONArray listArray, int utime){
        JSONObject obj=null;
        if(!(utime<=lastutime)) return null;
        try {
            int i;
            for (i=0; i<count; i++) {
                obj = listArray.getJSONObject(i);
                if (obj.getInt("dt")>=utime) break; // 要求時刻以降で最も時刻が近いデータを持ってくる
                obj=null;   // 求める時刻のデータが最後まで見つからなければnullを返す
            }
        } catch (JSONException e) {
            // not exist exception data in listArray
            e.printStackTrace();
            obj=null;
        }
        return obj;
    }

    public String getWeatherString(JSONObject obj){
        String weather=null;
        try {
            JSONArray mainArray = obj.getJSONArray("weather");
            JSONObject mainobj = mainArray.getJSONObject(0);
            weather = mainobj.getString("main");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }


    public int getCloudPercentage(JSONObject obj){
        int percent=-1;
        try {
            JSONObject cloudobj = obj.getJSONObject("clouds");
            percent = cloudobj.getInt("all");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return percent;
    }


    // get the amount of rain
    // use obj (returned by getWeatheObj(JSONArray)), cant use rootObj,
    public double getAmountRain(JSONObject obj){
        double amount=0.0;
        if( getWeatherString(obj) == "Rain") {
            try {
                JSONObject rainobj = obj.getJSONObject("rain");
                amount = rainobj.getDouble("3h");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return amount;
    }

    //get weather icon ic
    //use obj. cant use rootObj
    public String getWeatherIconId(JSONObject obj){
        String iconid;
        try {
            JSONArray mainArray = obj.getJSONArray("wather");
            JSONObject mainobj = mainArray.getJSONObject(0);
            iconid = mainobj.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
            iconid = null;
        }

        return iconid;
    }
}
