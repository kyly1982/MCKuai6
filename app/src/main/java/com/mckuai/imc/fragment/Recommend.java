/**
 * 
 */
package com.mckuai.imc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.imc.activity.LocationActivity;
import com.mckuai.imc.activity.LoginActivity;
import com.mckuai.imc.activity.MainActivity;
import com.mckuai.imc.activity.MyApplication;
import com.mckuai.imc.activity.PostActivity;
import com.mckuai.imc.R;
import com.mckuai.imc.activity.SearchResultActivity;
import com.mckuai.imc.activity.UserCenter;
import com.mckuai.imc.adapter.PostAdapter;
import com.mckuai.imc.bean.ChatRoom;
import com.mckuai.imc.bean.RecommendBean;
import com.mckuai.imc.bean.User;
import com.mckuai.imc.until.NetworkEngine;
import com.mckuai.imc.widget.CircleImageView;
import com.mckuai.imc.widget.XListView;
import com.mckuai.imc.widget.XListView.IXListViewListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.Serializable;

import io.rong.imkit.RongIM;

public class Recommend extends BaseFragment implements OnClickListener, OnItemClickListener, IXListViewListener,NetworkEngine.onRcLoginListener
{

	private RecommendBean data;
	private AsyncHttpClient client = MyApplication.getInstance().getClient();
	private String TAG = "Recommend";
	private Gson mGson = new Gson();
	private RelativeLayout mUserInfoLayout;
	private CircleImageView mUserView;
	private TextView tv_userName;
	private TextView tv_level;
	private TextView tv_location;

	private XListView list;
	private PostAdapter mAdapter;
	private View view;
	private User mUser;
	private boolean isReadytoShow = false;
	private boolean isLoading = false;
	private ChatRoom room;
	private MyApplication mApplication;
	private boolean isAskForLogin = false;
	private RongIM rongIM;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (null == view)
		{
			view = inflater.inflate(R.layout.fragment_recommend, null);
		}
		setTitle("推荐页");
		if (null == mApplication)
		{
			mApplication = MyApplication.getInstance();
			rongIM = mApplication.getRC();
		}
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.fragment.BaseFragment#initView()
	 */
	protected void initView()
	{
		// TODO Auto-generated method stub
		list = (XListView) view.findViewById(R.id.list);
		tv_userName = (TextView) view.findViewById(R.id.tv_UserName);
		tv_level = (TextView) view.findViewById(R.id.tv_UserLevel);
		tv_location = (TextView) view.findViewById(R.id.tv_Location);
		list.setPullRefreshEnable(true);
		list.setPullLoadEnable(false);
		list.setXListViewListener(this);
		list.setEmptyView(getActivity().findViewById(R.id.empty));
		mUserView = (CircleImageView) view.findViewById(R.id.prg_user_Head);
		mUserInfoLayout = (RelativeLayout) view.findViewById(R.id.rl_UserInfo);
		view.findViewById(R.id.btn_backPackage).setOnClickListener(this);
		view.findViewById(R.id.tv_Location).setOnClickListener(this);
		mUserView.setOnClickListener(this);
	}

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
		if (getUserVisibleHint())
		{
			if (isReadytoShow)
			{
				if (!isAskForLogin)
				{
					showData();
				}
			} else
			{
				isReadytoShow = true;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#setUserVisibleHint(boolean)
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser)
		{
			if (isReadytoShow)
			{
				showData();
			} else
			{
				isReadytoShow = true;
			}
		}
	}

	@Override
	public void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		mUser = mApplication.getUser();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.fragment.BaseFragment#showData()
	 */
	public void showData()
	{
		// TODO Auto-generated method stub
		//Log.e(TAG, "showData");
		setNotificationViewGroup(R.id.root);
		if (null == list)
		{
			initView();
		}
		if (null == data)
		{
			String result = mApplication.getRecommend();
			if (null != result && 10 < result.length())
			{
				parseResult(result);
				showData();
				return;
			} else
			{
				loadData();
				return;
			}

		} else
		{
			onload();
			if (null == mAdapter)
			{
				mAdapter = new PostAdapter(getActivity(), data.getChat(), data.getLive(), data.getTalk());
				list.setAdapter(mAdapter);
				list.setOnItemClickListener(this);
			} else
			{
				mAdapter.setData(data.getChat(), data.getLive(), data.getTalk());
			}
		}
		showUser();
	}

	public void showUser()
	{
		if (mApplication.isLogin() && mUser != null)
		{
			this.mUser = mApplication.getUser();
			ImageLoader loader = ImageLoader.getInstance();
			DisplayImageOptions options = mApplication.getCircleOptions();
			if (null == mUserView)
			{
				mUserView = (CircleImageView) view.findViewById(R.id.prg_user_Head);
			}
			loader.displayImage(mUser.getHeadImg() + "", mUserView, options);
			tv_userName.setText(mUser.getNike() + "");
			tv_level.setText("LV." + mUser.getLevel());
			tv_location.setText(mUser.getAddr() + "");
			mUserView.setProgress(mUser.getProcess());
			tv_level.setVisibility(View.VISIBLE);
			tv_location.setVisibility(View.VISIBLE);
		} else
		{
			mUserView.setImageResource(R.mipmap.background_user_cover_default);
			tv_userName.setText("点击头像登录");
			tv_level.setVisibility(View.GONE);
			tv_location.setVisibility(View.GONE);
			mUserView.setProgress(0);
		}
	}

	private void loadData()
	{
		final RequestParams params = new RequestParams();
		// TODO Auto-generated method stub
		if (mApplication.isLogin())
		{
			params.put("id", MyApplication.getInstance().getUser().getId());
		}
		final String url = getString(R.string.interface_domainName) + getString(R.string.interface_recommend);
		Log.e(TAG, url);
		client.get(url, params, new JsonHttpResponseHandler()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
			 */
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				if (null == data)
				{
					String result = getData(url, params);
					if (null != result && 10 < result.length())
					{
						parseResult(result);
						showData();
						return;
					}
				} else
				{
					popupProgressDialog(getString(R.string.onloading_hint));
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onSuccess
			 * (int, org.apache.http.Header[], org.json.JSONObject)
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub

				if (null != response && response.has("state") && response.has("dataObject"))
				{
					String data = null;
					try
					{
						data = response.getString("dataObject");
					} catch (Exception e)
					{
						// TODO: handle exception
						// showNotification("数据不正确！");
						Toast.makeText(getActivity(), "数据不正确！", Toast.LENGTH_SHORT).show();
						cancelProgressDialog(false);
						return;
					}
					if (null == data)
					{
						Toast.makeText(getActivity(), "取回的列表为空，请重试！", Toast.LENGTH_SHORT).show();
						// showNotification("取回的列表为空，请重试！");
						cancelProgressDialog(false);
						return;
					}
					cancelProgressDialog(true);
					parseResult(data);
					cacheData(url, params, data);
					showData();
					return;
				}
				cancelProgressDialog(false);
				Toast.makeText(getActivity(), "取回数据失败！", Toast.LENGTH_SHORT).show();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onFailure
			 * (int, org.apache.http.Header[], java.lang.Throwable,
			 * org.json.JSONObject)
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
			{
				// TODO Auto-generated method stub
				// super.onFailure(statusCode, headers, throwable,
				// errorResponse);
				if (null == getActivity())
				{
					return;
				}
				Toast.makeText(getActivity(), "获取数据失败！原因：" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT)
						.show();
				cancelProgressDialog(false);
				// showNotification(throwable.getLocalizedMessage());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId())
		{
		case R.id.prg_user_Head:
			if (MyApplication.getInstance().isLogin())
			{
				intent = new Intent(getActivity(), UserCenter.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(getString(R.string.user), MyApplication.getInstance().getUser());
				intent.putExtras(bundle);
				startActivity(intent);
			} else
			{
				callLogin(1);
			}
			break;
		case R.id.btn_backPackage:
			MobclickAgent.onEvent(getActivity(), "openBackPackage_Rec");
			if (MyApplication.getInstance().isLogin())
			{
				intent = new Intent(getActivity(), SearchResultActivity.class);
				startActivity(intent);
			} else
			{
				callLogin(2);
			}
			break;
		case R.id.tv_Location:
			MobclickAgent.onEvent(getActivity(), "updateLocation_rec");
			intent = new Intent(getActivity(), LocationActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void parseResult(String result)
	{
		data = mGson.fromJson(result, RecommendBean.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		position = position - 1;
		switch (mAdapter.getItemViewType(position))
		{
		case 0:
			MobclickAgent.onEvent(getActivity(), "joinChatRoom");
			room = (ChatRoom) mAdapter.getItem(position);
			if (mApplication.isLogin())
			{
				if (null != room)
				{
					joinChatRoom();
				}
			} else
			{
				callLogin(3);
			}

			break;

		default:
			Intent intent = new Intent(getActivity(), PostActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(getString(R.string.tag_post), (Serializable) mAdapter.getItem(position));
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
	}

	private void onload()
	{
		list.stopLoadMore();
		list.stopRefresh();
	}

	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		onload();
		loadData();
	}

	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub

	}

	private void joinChatRoom()
	{
		rongIM = mApplication.getRC();
		if (null == rongIM)
		{
			MainActivity.getInstance().initRC();
			mHandler.sendMessageDelayed(mHandler.obtainMessage(4), 500);
			return;
		}
		if (mApplication.isLogin() && !mApplication.isLoginRC())
		{
			mApplication.networkEngine.loginToRC(this);
		}
		if (null != mUser && 0 != mUser.getId())
		{
			//rongIM.startChatroom(getActivity(), room.getId() + "", room.getName() + "");
		} else
		{
			callLogin(3);
		}
	}

	private void callLogin(int action)
	{
		isAskForLogin = true;
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.putExtra(getString(R.string.needLoginResult), true);
		startActivityForResult(intent, action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.BaseActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 1000);
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent;
		if (resultCode == Activity.RESULT_OK)
		{
			switch (requestCode)
			{
			case 1:
				showUser();
				break;

			case 2:
				intent = new Intent(getActivity(), SearchResultActivity.class);
				startActivity(intent);
				break;
			case 3:
				joinChatRoom();
				break;

			default:
				break;
			}
		} else
		{
			switch (requestCode)
			{
			case 1:
				Toast.makeText(getActivity(), "登录后才能查看个人中心!", Toast.LENGTH_SHORT).show();
				// showNotification("登录后才能查看个人中心!");
				break;
			case 2:
				// showNotification("登录后才能查看个人背包!");
				Toast.makeText(getActivity(), "登录后才能查看个人背包!", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				// showNotification("登录后才能聊天!");
				Toast.makeText(getActivity(), "登录后才能聊天!", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	}

	Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case 0:
				isAskForLogin = false;
				break;
			case 4:
				joinChatRoom();
				break;

			default:
				break;
			}

		};
	};

	@Override
	public void onSuccess() {
        joinChatRoom();
	}

	@Override
	public void onFalse(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
	}
}
