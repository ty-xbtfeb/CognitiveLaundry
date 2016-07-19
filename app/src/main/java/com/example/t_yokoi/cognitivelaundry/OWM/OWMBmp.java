package com.example.t_yokoi.cognitivelaundry.OWM;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by masa on 2016/07/08.
 */
public class OWMBmp{
    final String basicURL="http://openweathermap.org/img/w/";

    // iconid is getted by getWeatherDayicon(JSONArray, int) in OWMAct.java
    public Bitmap getIconBitmap(String iconid){
        String requestURL = basicURL + iconid.substring(0,2) + "d.png";
        Bitmap icon=null;
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
            icon = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return icon;
    }
}
