package com.mckuai.imc.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.mckuai.imc.R;
import com.mckuai.imc.until.JsonCache;
import com.mckuai.imc.widget.LoadToast;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.sso.UMSsoHandler;

import java.lang.reflect.Field;

public abstract class BaseActivity extends FragmentActivity
{

	public static final String TAG = "BaseActivity";

	protected AlertDialog mAlertDialog;
	protected AsyncTask mRunningTask;
	private static JsonCache mCache = MyApplication.getInstance().getCache();
	private String mTitle;
	private static LoadToast waitDialog;
	private Configuration cfg;
	private int viewGroupResId;
	private Point point;
	protected boolean isShowingMenu = false;
	protected static com.umeng.socialize.controller.UMSocialService mShareService = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	/******************************** 【Activity LifeCycle For Debug】 *******************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
		MobclickAgent.onResume(this);
		if (0 == viewGroupResId)
		{
			if (null != findViewById(R.id.root))
			{
				setNotificationViewGroup(R.id.root);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if (mRunningTask != null && mRunningTask.isCancelled() == false)
		{
			mRunningTask.cancel(false);
			mRunningTask = null;
		}
		if (mAlertDialog != null)
		{
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}
		if (null != waitDialog)
		{
			waitDialog.error();
			waitDialog = null;
		}
	}

	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	public void recommandToYourFriend(String url, String shareTitle)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, shareTitle + "   " + url);

		Intent itn = Intent.createChooser(intent, "分享");
		startActivity(itn);
	}

	/******************************** 【Activity LifeCycle For Debug】 *******************************************/

	protected void setNotificationViewGroup(int viewGroupResId)
	{
		this.viewGroupResId = viewGroupResId;
	}

	protected void showNotification(final int level,final String msg,final int rootId) {

	}


	private String getColorString(int colorResId)
	{
		int color = getResources().getColor(colorResId);
		String c = "#" + Integer.toHexString(color);
		return c.toUpperCase();
	}

	protected boolean hasExtra(String pExtraKey)
	{
		if (getIntent() != null)
		{
			return getIntent().hasExtra(pExtraKey);
		}
		return false;
	}

	protected void openActivity(Class<?> pClass)
	{
		openActivity(pClass, null);
	}

	protected void openActivity(Class<?> pClass, Bundle pBundle)
	{
		Intent intent = new Intent(this, pClass);
		if (pBundle != null)
		{
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	protected void openActivity(String pAction)
	{
		openActivity(pAction, null);
	}

	protected void openActivity(String pAction, Bundle pBundle)
	{
		Intent intent = new Intent(pAction);
		if (pBundle != null)
		{
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过反射来设置对话框是否要关闭，在表单校验时很管用， 因为在用户填写出错时点确定时默认Dialog会消失， 所以达不到校验的效果
	 * 而mShowing字段就是用来控制是否要消失的，而它在Dialog中是私有变量， 所有只有通过反射去解决此问题
	 * 
	 * @param pDialog
	 * @param pIsClose
	 */
	public void setAlertDialogIsClose(DialogInterface pDialog, Boolean pIsClose)
	{
		try
		{
			Field field = pDialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(pDialog, pIsClose);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public AlertDialog showAlertDialog(String TitleID, String Message)
	{
		mAlertDialog = new AlertDialog.Builder(this).setTitle(TitleID).setMessage(Message).show();
		return mAlertDialog;
	}

	protected AlertDialog showAlertDialog(int pTitelResID, String pMessage,
			DialogInterface.OnClickListener pOkClickListener)
	{
		String title = getResources().getString(pTitelResID);
		return showAlertDialog(title, pMessage, pOkClickListener, null, null);
	}

	protected AlertDialog showAlertDialog(String pTitle, String pMessage,
			DialogInterface.OnClickListener pOkClickListener, DialogInterface.OnClickListener pCancelClickListener,
			DialogInterface.OnDismissListener pDismissListener)
	{
		mAlertDialog = new AlertDialog.Builder(this).setTitle(pTitle).setMessage(pMessage)
				.setPositiveButton(android.R.string.ok, pOkClickListener)
				.setNegativeButton(android.R.string.cancel, pCancelClickListener).show();
		if (pDismissListener != null)
		{
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	protected AlertDialog showAlertDialog(String pTitle, String pMessage, String pPositiveButtonLabel,
			String pNegativeButtonLabel, DialogInterface.OnClickListener pOkClickListener,
			DialogInterface.OnClickListener pCancelClickListener, DialogInterface.OnDismissListener pDismissListener)
	{
		mAlertDialog = new AlertDialog.Builder(this).setTitle(pTitle).setMessage(pMessage)
				.setPositiveButton(pPositiveButtonLabel, pOkClickListener)
				.setNegativeButton(pNegativeButtonLabel, pCancelClickListener).show();
		if (pDismissListener != null)
		{
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	protected ProgressDialog showProgressDialog(int pTitelResID, String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener)
	{
		String title = getResources().getString(pTitelResID);
		return showProgressDialog(title, pMessage, pCancelClickListener);
	}

	protected ProgressDialog showProgressDialog(String pTitle, String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener)
	{
		mAlertDialog = ProgressDialog.show(this, pTitle, pMessage, true, true);
		mAlertDialog.setOnCancelListener(pCancelClickListener);
		return (ProgressDialog) mAlertDialog;
	}

	protected void hideKeyboard(View view)
	{
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	protected void showKeyboard(View view){
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	protected void handleOutmemoryError()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(BaseActivity.this, "内存空间不足！", Toast.LENGTH_SHORT).show();
				// finish();
			}
		});
	}

	private int network_err_count = 0;

	protected void handleNetworkError()
	{
		network_err_count++;
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (network_err_count < 3)
				{
					Toast.makeText(BaseActivity.this, "网速好像不怎么给力啊！", Toast.LENGTH_SHORT).show();
				} else if (network_err_count < 5)
				{
					Toast.makeText(BaseActivity.this, "网速真的不给力！", Toast.LENGTH_SHORT).show();
				} else
				{
					Toast.makeText(BaseActivity.this, "唉，今天的网络怎么这么差劲！", Toast.LENGTH_SHORT).show();
				}
				// finish();
			}
		});
	}

	protected void handleMalformError()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(BaseActivity.this, "数据格式错误！", Toast.LENGTH_SHORT).show();
				// finish();
			}
		});
	}

	protected void handleFatalError()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(BaseActivity.this, "发生了一点意外，程序终止！", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	public void defaultFinish()
	{
		super.finish();
	}

	public void ToastInfo(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void popupProgressDialog(String popMsg)
	{
		if (null == waitDialog)
		{
			if (null == point)
			{
				point = new Point();
				getWindowManager().getDefaultDisplay().getSize(point);
			}
			waitDialog = new LoadToast(this);
			waitDialog.setTranslationY((int) (point.y * 0.4));
			waitDialog.setTextColor(getResources().getColor(R.color.font_white)).setBackgroundColor(
					getResources().getColor(R.color.background_green));
		}
		// 设定消息
		waitDialog.setText(popMsg);
		// 现实进度对话框
		waitDialog.show();
		mHandler.sendMessageDelayed(mHandler.obtainMessage(2), 16000);
	}

	public void cancelProgressDialog(boolean issucess)
	{
		if (null != waitDialog)
		{
			if (issucess)
			{
				waitDialog.success();
			} else
			{
				waitDialog.error();
			}
		}

	}

	Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (null != waitDialog)
			{
				cancelProgressDialog(false);
			}
		};
	};

	/**
	 * 将数据缓存到缓存中
	 * 
	 * @param url
	 *            数据所归属的url
	 * @param data
	 *            要缓存的数据
	 */
	public static void cacheData(String url, String data)
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
	public static void cacheData(String url, RequestParams params, String data)
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
	public static String getData(String url)
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
	public static String getData(String url, RequestParams params)
	{
		if (null == params)
		{
			return mCache.get(url);
		} else
		{
			return mCache.get((url + "&" + params.toString()));
		}
	}

	public void setTitle(String mTitle)
	{
		this.mTitle = mTitle;
	}

//	protected void sharePost(Post post)
//	{
//		if (null == post)
//		{
//			return;
//		}
//		mShareService.setShareContent(post.getTalkTitle());
//		if (null != post.getMobilePic() || 10 < post.getMobilePic().length())
//		{
//			configPlatforms(post);
//		}
//		mShareService.setShareContent("test");
//		mShareService.setShareMedia(new UMImage(this, post.getMobilePic()));
//		mShareService.openShare(this, false);
//	}
//	
//	private void configPlatforms(Post post){
//		String targetUrl = "http://www.mckuai.com/thread-"+post.getId() + ".html";
//		String title = "麦块for我的世界盒子";
//		
//		String appID_QQ = "101155101";
//		String appAppKey_QQ = "78b7e42e255512d6492dfd135037c91c";
//		//添加qq
//		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appID_QQ, appAppKey_QQ);
//		qqSsoHandler.setTargetUrl(targetUrl);
//		qqSsoHandler.setTitle(title);
//		qqSsoHandler.addToSocialSDK();
//		// 添加QQ空间参数
//		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appID_QQ, appAppKey_QQ);
//		qZoneSsoHandler.setTargetUrl(targetUrl);
//		qZoneSsoHandler.addToSocialSDK();
//		
//		String appIDWX = "wx49ba2c7147d2368d";
//		String appSecretWX = "85aa75ddb9b37d47698f24417a373134";
//		//添加微信
//		UMWXHandler wxHandler = new UMWXHandler(this, appIDWX, appSecretWX);
//		wxHandler.setTargetUrl(targetUrl);
//		wxHandler.setTitle(title);
//		wxHandler.addToSocialSDK();
//		//添加微信朋友圈
//		UMWXHandler wxCircleHandler = new UMWXHandler(this, appIDWX, appSecretWX);
//		wxCircleHandler.setToCircle(true);
//		wxCircleHandler.setTargetUrl(targetUrl);
//		wxCircleHandler.setTitle(title);
//		wxCircleHandler.addToSocialSDK();
//		//移除多余平台
//		mShareService.getConfig().removePlatform(SHARE_MEDIA.TENCENT,SHARE_MEDIA.SINA);
//	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		UMSsoHandler ssoHandler = mShareService.getConfig().getSsoHandler(requestCode);
		if (null != ssoHandler)
		{
			Log.e(TAG, "get ssoHandler");
			 ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}


	/**
	 * 重写此方法时请勿调用super方法
	 *
	 * @return
	 */
	protected boolean onMenuKeyPressed() {
		return false;
	}

	/**
	 * 重写此方法时请勿调用super方法
	 *
	 * @return
	 */
	protected boolean onBackKeyPressed() {
		return false;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (onMenuKeyPressed()) {
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (onBackKeyPressed()){
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
