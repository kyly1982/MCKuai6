package com.mckuai.imc.widget;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.Chronometer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Anticlock extends Chronometer {

	private long mTime;
	private long mNextTime;
	private SimpleDateFormat mTimeFormat;
	private OnTimeCompleteListener mListener;
	
	private static long MAILLIS_IN_DAY = 60 * 60 * 24;//一天的毫秒数
	
	public Anticlock(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mTimeFormat = new SimpleDateFormat("HH:mm:ss");
		mTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT0:00"));
		this.setOnChronometerTickListener(listener);
		@SuppressWarnings("deprecation")
		Time time = new Time();
		time.set(System.currentTimeMillis());
		initTime((23 - time.hour) * 3600 + (60 - time.minute) * 60 + (60 - time.second));
		start();
	}
	
	

	public void reStart(long _time_s) {
		if (_time_s == -1) {
			mNextTime = mTime;
		} else {
			mTime = mNextTime = _time_s;
		}
		this.start();
	}

	public void reStart() {
		reStart(-1);
	}

	/**
	 * 继续计时
	 */
	public void onResume() {
		this.start();
	}

	/**
	 * 暂停计时
	 */
	public void onPause() {
		this.stop();
	}

	/**
	 * 初始化时间
	 * 
	 * @param _time_s
	 */
	public void initTime(long _time_s)  
    {  
        mTime = mNextTime = _time_s;  
        updateTimeText();  
    } 

	public void setOnTimeCompleteListener(OnTimeCompleteListener l) {
		mListener = l;
	}

	private void updateTimeText() {
		this.setText(mTimeFormat.format(new Date(mNextTime * 1000)));
	}

	OnChronometerTickListener listener = new OnChronometerTickListener() {
		@Override
		public void onChronometerTick(Chronometer chronometer) {
			if (mNextTime <= 0) {
				if (mNextTime == 0) {
					Anticlock.this.stop();
					if (null != mListener)
						mListener.onTimeComplete();
				}
				mNextTime = 0;
				updateTimeText();
				return;
			}
			mNextTime--;
			updateTimeText();
		}
	};

	interface OnTimeCompleteListener {
		void onTimeComplete();
	}

}
