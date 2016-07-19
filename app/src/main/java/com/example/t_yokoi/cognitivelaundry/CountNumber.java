package com.example.t_yokoi.cognitivelaundry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CountNumber {

	// 残数を5日分保存する配列
	static int[] remain = new int[5];
	// 服の所持数
	static int max;

	// 日ごとの洗濯優先度
	static double[] priority = new double[5];

	final static int MILLIS_OF_DAY = 1000 * 60 * 60 * 24;
	final static long MILLIS_OF_HOUR = 3600000;
	//  日本の時差
	final static int jptime = 9 * 1000 * 60 * 60;
	// 前回起動時間を保持
	static int prev_hour;
	static int prev_minute;
	static long prev_time;
	// 入浴時間
	static int bath_hour;
	static int bath_min;

	// 初期化
	public static void setAll(int m, int bh, int bm, int r) {
		final Calendar calendar = Calendar.getInstance();
		prev_hour = calendar.get(Calendar.HOUR_OF_DAY);
		prev_minute = calendar.get(Calendar.MINUTE);
		prev_time = calendar.getTimeInMillis();
		max = m;
		bath_hour = bh;
		bath_min = bm;
		remain[0] = r;
		System.out.println("max= " + max + ", bath= " + bath_hour + ":" + bath_min + ", now= " + remain[0] + "\n");
	}

	public static void main(String[] args) {
		// 7枚持ち，入浴，現在の残数4枚
		setAll(7, 13, 21, 3);
		CountNumber cn = new CountNumber();
		while (true) {
			remain_minus();
			remain_new();
			String s = String.valueOf(remain[0]);
			for (int i = 1; i < 5; i++) {
				s += ", " + String.valueOf(remain[i]);
			}
			System.out.println("	remain list; " + s);
			recommend(priority);
			laundry();
			cn.sleep(prev_time);
		}
	}

	// 残数が更新されたら，他の日の表示も更新
	public static void remain_new() {
		for (int i = 0; i < remain.length; i++) {
			if (i <= remain[0])
				remain[i] = remain[0] - i;
			else
				remain[i] = 0;
		}
	}

	// 5日それぞれの優先度を受け取り，残数が0になる前まで，あるいは5日間のうち優先度が最大のものを選ぶ
	// 優先度が同じなら後ろの日に洗濯
	public static int recommend(double[] priority) {
		// ほんとはどこかでちゃんと優先度を計算する
		for (int i = 0; i < 5; i++) {
			priority[i] = Math.random();
		}

		double pmax = 0;
		int maxi = 0;
		int date;
		if (remain[0] >= 5)
			date = 5;
		else
			date = remain[0];
		for (int i = 0; i < date; i++) {
			if (pmax <= priority[i]) {
				pmax = priority[i];
				maxi = i;
			}
		}
		System.out.println("	recommend; " + maxi + "日目");
		return maxi;
	}

	// 前回取得した時間から残数がどれだけ減っているか
	public static void remain_minus() {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		// 前回の起動から入浴時間を何回超えたか（つまり前回の残数よりいくつ減ったか）
		int count = 0;

		// 前回時刻が入浴時間前かつ今回時刻が入浴時間後
		if ((hour > bath_hour || (hour == bath_hour && minute >= bath_min))
				&& (prev_hour < bath_hour || (prev_hour == bath_hour && prev_minute < bath_min)))
			count++;
		// 前回時刻が入浴後かつ今回時刻が入浴前
		if ((hour < bath_hour || (hour == bath_hour && minute < bath_min))
				&& (prev_hour > bath_hour || (prev_hour == bath_hour && prev_minute >= bath_min)))
			count--;

		// 日数分減らす
		count += getDiffDays(calendar.getTimeInMillis(), prev_time);

		if (count > remain[0])
			remain[0] = 0;
		else
			remain[0] -= count;

		DateFormat df0 = new SimpleDateFormat("yyyy/MM/dd　HH:mm:ss");
		System.out.print(df0.format(prev_time) + " -> ");

		prev_time = calendar.getTimeInMillis();

		Date date = new Date(prev_time);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd　HH:mm:ss");
		System.out.println(df.format(date));

		prev_hour = hour;
		prev_minute = minute;

	}

	// 経過日数取得
	public static long getDiffDays(long time, long prev_time) {
		// 日単位に変換
		long timeDays = (time + jptime) / MILLIS_OF_DAY;
		long timePrevDays = (prev_time + jptime) / MILLIS_OF_DAY;
		long diffDays = timeDays - timePrevDays;
		// System.out.println("now " + timeDays + ", prev= " + timePrevDays + ",
		// " + diffDays);
		return diffDays;

	}

	public static void laundry() {
		if (remain[0] == 1) { // 実際には洗濯ボタンが押されたら
			remain[0] = max - 1;
			remain_new();
			System.out.println("clear!");
		}
	}

	// 1時間ごとに時刻を取得する
	public synchronized void sleep(long now_time) {
		long msec;
		long bath = bath_min * 1000 * 60;
		long now = now_time % MILLIS_OF_HOUR;
		if (now >= bath)
			msec = MILLIS_OF_HOUR - (now - bath);
		else
			msec = bath - now;
		// System.out.println(msec);
		try {
			wait(msec);
		} catch (InterruptedException e) {
		}
	}
}
