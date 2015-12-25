package com.mckuai.imc.fragment;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.mckuai.imc.R;
import com.mckuai.imc.activity.MyApplication;
import com.mckuai.imc.until.JsonCache;
import com.mckuai.imc.widget.LoadToast;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment
{
	private String TAG = "BaseFragment";
	private String mTitle;
	private int mIndex;
	private static LoadToast waitDialog;
	private int viewGroupResId;
	private Point point;

	private Thread thread;
	private JsonCache mCache = MyApplication.getInstance().getCache();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(mTitle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mTitle);
		if (null != waitDialog)
		{
			waitDialog.error();
			waitDialog = null;
		}
	}

	public String getTitle()
	{
		return mTitle;
	}

	public void setTitle(String title)
	{
		mTitle = title;
	}

	public int getIndex()
	{
		return mIndex;
	}

	public void setIndex(int index)
	{
		this.mIndex = index;
	}

	protected void showNotification(String msg)
	{

	}

	protected void showNotification(int viewGroupId, String msg)
	{

	}

	protected void setNotificationViewGroup(int viewGroupResId)
	{
		this.viewGroupResId = viewGroupResId;
	}



	private String getColorString(int colorResId)
	{
		int color = getResources().getColor(colorResId);
		String c = "#" + Integer.toHexString(color);
		return c.toUpperCase();
	}

	public void popupProgressDialog(String popMsg)
	{
		// 打开 ProgressDialog
		if (null == waitDialog)
		{
			if (null == point)
			{
				point = new Point();
				getActivity().getWindowManager().getDefaultDisplay().getSize(point);
			}
			waitDialog = new LoadToast(getActivity());
			waitDialog.setTranslationY((int) (point.y * 0.4));
			waitDialog.setTextColor(getResources().getColor(R.color.font_white)).setBackgroundColor(
					getResources().getColor(R.color.background_green));
		}
		// 设定消息
		waitDialog.setText(popMsg);
		// 设定进度显示风格（此处为循环风格）
		// 现实进度对话框
		waitDialog.show();
		mHandler.removeMessages(4482);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 6000);
	}

	public void cancelProgressDialog(boolean isSuccess)
	{
		// 取消 ProgressDialog
		if (null != waitDialog)
		{
			if (isSuccess)
			{
				waitDialog.success();
			} else
			{
				waitDialog.error();
			}
			waitDialog = null;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 4482:
				Log.e(TAG, "超时失败!");
				cancelProgressDialog(false);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 将数据缓存到缓存中
	 * 
	 * @param url
	 *            数据所归属的url
	 * @param data
	 *            要缓存的数据
	 */
	public void cacheData(String url, String data)
	{
		mCache.put(url, data);
	}

	/**
	 * 将数据缓存到缓存中
	 * 
	 * @param url
	 *            数据所归属的url
	 * @param params
	 *            对应的请求参数
	 * @param data
	 *            要缓存的数据
	 */
	public void cacheData(String url, RequestParams params, String data)
	{
		if (null == params)
		{
			mCache.put(url, data);
		} else
		{
			mCache.put((url + "&" + params.toString()), data);
		}
	}

	/**
	 * 从缓存中获取对应url缓存下来的数据
	 * 
	 * @param url
	 *            要获取的数据对应的url
	 * @return 如果对应的url有缓存数据,则返回缓存数据,否则返回空
	 */
	public String getData(String url)
	{
		return mCache.get(url);
	}

	/**
	 * 从缓存中获取对应url缓存下来的数据
	 * 
	 * @param url
	 *            要获取的数据对应的url
	 * @param params
	 *            对应url的参数
	 * @return 如果对应的url有缓存数据,则返回缓存数据,否则返回空
	 */
	public String getData(String url, RequestParams params)
	{
		if (null == params)
		{
			return mCache.get(url);
		} else
		{
			return mCache.get((url + "&" + params.toString()));
		}
	}

}
