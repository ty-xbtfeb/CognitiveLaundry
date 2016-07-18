package com.example.t_yokoi.cognitivelaundry;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by t-yokoi on 2016/07/11.
 */
public class Day {
    public TextView date;
    public ImageView weather;
    public TextView washable;
    public TextView leftClothes;

    public Day() {
        TextView date = null;
        ImageView weather = null;
        TextView washable = null;
        TextView leftClothes = null;
    }

    public Day(TextView tvDate, ImageView iv, TextView washId) {
        date = tvDate;
        weather = iv;
        washable = washId;
    }

}
