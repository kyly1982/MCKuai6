package com.mckuai.imc.activity;

/**
 * 启动页
 * 显示启动页面，检查更新，控制是否显示引导页
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.mckuai.imc.R;
import com.mckuai.imc.activity.MyApplication.OnHttpResopnseListener;
import com.mckuai.imc.widget.autoupdate.ResponseParser;
import com.mckuai.imc.widget.autoupdate.Version;
import com.mckuai.imc.widget.autoupdate.internal.SimpleJSONParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class StartActivity extends Activity implements OnHttpResopnseListener
{
	private ImageLoader loader;
	private DisplayImageOptions options;
	private int returnOkCount = 0;
	private int returnCount = 0;
	// private AppUpdate updateChecker;
	private long mStartTime;
	private boolean isShowNext = false;
	private MyApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888).delayBeforeLoading(150)
				.showStubImage(R.drawable.loading).build();
		loader = ImageLoader.getInstance();
		application = MyApplication.getInstance();
		// updateChecker = AppUpdateService.getAppUpdate(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		// checkUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		mStartTime = System.currentTimeMillis();
		ImageView ad = (ImageView) findViewById(R.id.iv_ad);
		application.loadForums(this);
		application.loadRecommend(this);
		if (application.isLogin())
		{
			application.loadFirends(null);
		}
		loader.displayImage("http://cdn.mckuai.com/app_start.png", ad, options);
		handler.sendMessageDelayed(handler.obtainMessage(9998), 10000);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.onPageStart("启动页");
	}

	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd("启动页");
	};

	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case 9998:
				if (!isShowNext)
				{
					startNext();
				}
				break;

			default:
				super.handleMessage(msg);
				break;
			}
		};
	};

	private void startNext()
	{
		isShowNext = true;
		if (MyApplication.getInstance().isFirstBoot())
		{
			Intent intent = new Intent(StartActivity.this, LeadActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MyApplication.getInstance().setFirstBoot();
			startActivity(intent);
			finish();
		} else
		{
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onHttpResponse(boolean isSucess, String msg, int requestCode)
	{
		// TODO Auto-generated method stub
		returnCount++;

		if (isSucess && null == msg)
		{
			returnOkCount++;
			Log.e("returnOkCount=", returnOkCount + "");
			if (2 == returnOkCount)
			{
				handler.removeMessages(9998);
				long time = 4000 - (System.currentTimeMillis() - mStartTime);
				if (time > 0)
				{
					Log.e("time1=", time + "");
					handler.sendMessageDelayed(handler.obtainMessage(9998), time);
				} else
				{
					Log.e("time1=", 0 + "");
					handler.sendEmptyMessage(9998);
				}
				return;
			}
		}
		Log.e("returnCount=", returnCount + "");
		if (4 == returnCount)
		{
			handler.removeMessages(9998);
			long time = 4000 - (System.currentTimeMillis() - mStartTime);
			if (time > 0)
			{
				Log.e("time2=", time + "");
				handler.sendMessageDelayed(handler.obtainMessage(9998), time);
			} else
			{
				Log.e("time2=", 0 + "");
				handler.sendEmptyMessage(9998);
			}
		}
	}

	class MyJsonParser extends SimpleJSONParser implements ResponseParser
	{
		@Override
		public Version parser(String response)
		{
			try
			{
				JSONTokener jsonParser = new JSONTokener(response);
				JSONObject json = (JSONObject) jsonParser.nextValue();
				Version version = null;
				if (json.has("state") && json.has("dataObject"))
				{
					JSONObject dataField = json.getJSONObject("dataObject");
					int code = dataField.getInt("code");
					String name = dataField.getString("name");
					String feature = dataField.getString("feature");
					String targetUrl = dataField.getString("targetUrl");
					version = new Version(code, name, feature, targetUrl);
				}
				return version;
			} catch (JSONException exp)
			{
				exp.printStackTrace();
				return null;
			}
		}
	}
}
