package com.example.t_yokoi.cognitivelaundry;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.t_yokoi.cognitivelaundry.OWM.WeatherCon;

/**
 * Created by t-yokoi on 2016/07/19.
 */
public class AsyncHttpRequest extends AsyncTask<String, String, WeatherCon> {

    private Activity mainActivity;

    public AsyncHttpRequest(Activity activity) {
        super();
        // 呼び出し元のアクティビティ
        this.mainActivity = activity;
    }

    // このメソッドは必ずオーバーライドする必要があるよ
    // ここが非同期で処理される部分みたいたぶん。
    @Override
    protected WeatherCon doInBackground(String... s) {
        // httpリクエスト投げる処理を書く。
        // ちなみに私はHttpClientを使って書きましたー
        WeatherCon wc = new WeatherCon();
        wc.setWeatherData(s[0]);
        return wc;
    }


    // このメソッドは非同期処理の終わった後に呼び出されます
    @Override
    protected void onPostExecute(WeatherCon wc) {
        Resources res = mainActivity.getResources();
        for (int i = 0; i < 4; i++) {
            Log.v("rain", "" + wc.getAmountRain(i));
        }

        // 取得した結果をテキストビューに入れちゃったり
        for (int i = 0; i < MainActivity.maxDays; i++) {
            int weatherId = res.getIdentifier("weather_" + (i + 1), "id", mainActivity.getPackageName());
            MainActivity.days[i].weather = (ImageView) mainActivity.findViewById(weatherId);
            Bitmap bitmap = wc.getBitmap(i);
            if (MainActivity.days[i].weather != null) {
                // TODO: 2016/07/11 天候に応じた画像を表示させる
//                MainActivity.days[i].weather.setImageResource(res.getIdentifier("sun", "mipmap", mainActivity.getPackageName()));
                MainActivity.days[i].weather.setImageBitmap(bitmap);
            }
        }

    }
}