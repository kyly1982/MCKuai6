/**
 * 
 */
package com.mckuai.imc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.imc.R;
import com.mckuai.imc.activity.MainActivity;
import com.mckuai.imc.activity.MyApplication;
import com.mckuai.imc.activity.PostActivity;
import com.mckuai.imc.adapter.PostAdapter;
import com.mckuai.imc.bean.LiveBean;
import com.mckuai.imc.bean.PageInfo;
import com.mckuai.imc.bean.Post;
import com.mckuai.imc.widget.XListView;
import com.mckuai.imc.widget.XListView.IXListViewListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class Live extends BaseFragment implements OnCheckedChangeListener, OnItemSelectedListener, IXListViewListener,
		OnItemClickListener
{
	private XListView list;
	private static ArrayList<Post> mNew = new ArrayList<Post>(60);
	private static ArrayList<Post> mHot = new ArrayList<Post>(60);
	private static ArrayList<Post> mAll = new ArrayList<Post>(60);
	private static ArrayList<Post> mLivePostsList;
	private static PageInfo mPageNew = new PageInfo();
	private static PageInfo mPageHot = new PageInfo();
	private static PageInfo mPageAll = new PageInfo();
	private static PageInfo mPage;
	private String mGroupType[] = { "new", "hot", "all" };
	private String liveType[];
	private String mGroup = mGroupType[0];// 类型
	private String mLiveType = null;
	private Spinner spinner;
	private Gson mGson = new Gson();
	private AsyncHttpClient mClient;
	private PostAdapter mAdapter;
	private View view;
	private boolean isLoading = false;

	private static final String TAG = "Live";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (null == view)
		{
			view = inflater.inflate(R.layout.fragment_live, container, false);
			mClient = MyApplication.getInstance().getClient();
			mLivePostsList = mNew;
			mPage = mPageNew;
			liveType = getResources().getStringArray(R.array.live_type);
		}
		setTitle("直播页");
		return view;
	}

	protected void initView()
	{
		// TODO Auto-generated method stub
		list = (XListView) view.findViewById(R.id.lv_postList_live);
		list.setPullRefreshEnable(true);
		list.setPullLoadEnable(false);
		list.setXListViewListener(this);
		list.setOnItemClickListener(this);
		list.setEmptyView(getActivity().findViewById(R.id.empty));
		((RadioGroup) view.findViewById(R.id.rg_indicator)).setOnCheckedChangeListener(this);
		((RadioButton) view.findViewById(R.id.rb_now)).setChecked(true);
		spinner = ((MainActivity) getActivity()).getSpinner();
		if (null != spinner)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
					R.array.live_type_simple, R.layout.item_spinner);
			adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
		}
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		if (getUserVisibleHint())
		{
			showData();
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
			showData();
		}
	}

	public void showData()
	{
		if (null == list)
		{
			initView();
		}
		if (null == mLivePostsList || (mLivePostsList.isEmpty() && 0 == mPage.getPage()))
		{
			loadData();
		} else
		{
			if (null == list)
			{
				initView();
			}
			if (null == mAdapter)
			{
				mAdapter = new PostAdapter(getActivity(), mLivePostsList);
				list.setAdapter(mAdapter);
			} else
			{
				if (0 != mLivePostsList.size())
				{
					mAdapter.setData(mLivePostsList);
				} else
				{
					mAdapter.notifyDataSetInvalidated();
				}
			}
			if (mPage.getPage() < mPage.getPageCount())
			{
				list.setPullLoadEnable(true);
			} else
			{
				list.setPullLoadEnable(false);
			}
		}
	}

	private void loadData()
	{
		// TODO Auto-generated method stub
		if (isLoading)
		{
			return;
		}
		isLoading = true;
		final RequestParams params = new RequestParams();
		if (null != mLiveType)
		{
			params.put("type", URLEncoder.encode(mLiveType));
		}
		params.put("orderField", mGroup);
		if (null != mPage && 0 != mPage.getAllCount())
		{
			params.put("page", mPage.getNextPage() + "");
		} else
		{
			params.put("page", "1");
		}
		final String url = getString(R.string.interface_domainName) + getString(R.string.interface_live);
		mClient.post(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				if (1 == mPage.getNextPage())
				{
					String result = getData(url, params);
					if (null != result && 10 < result.length())
					{
						Log.e(TAG, "从缓存中获取到了数据!");
						showData();
						return;
					}
				}
				popupProgressDialog(getString(R.string.onloading_hint));
				Log.e(TAG, url + "&" + params.toString());
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				Toast.makeText(getActivity(), "0获取数据失败!原因:" + responseString, Toast.LENGTH_SHORT).show();
				isLoading = false;
				cancelProgressDialog(false);
			}
			
			/* (non-Javadoc)
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onFailure(int, org.apache.http.Header[], java.lang.Throwable, org.json.JSONObject)
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Toast.makeText(getActivity(), "1获取数据失败!原因:" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
				isLoading = false;
				cancelProgressDialog(false);
			}
			
			/* (non-Javadoc)
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onFailure(int, org.apache.http.Header[], java.lang.Throwable, org.json.JSONArray)
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
				cancelProgressDialog(false);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				isLoading = false;
				super.onSuccess(statusCode, headers, response);
				cancelProgressDialog(true);
				if (null != response && response.has("state"))
				{
					try
					{
						if (response.getString("state").equalsIgnoreCase("ok")
								&& 10 < response.getString("dataObject").length())
						{
							Log.e(TAG, "从网络中获取到数据!");
							parseResult(response.getString("dataObject"));
							showData();
							if (1 == mPage.getPage())
							{
								cacheData(url, params, response.getString("dataObject"));
							}
							return;
						}
					} catch (Exception e)
					{
						// TODO: handle exception
						e.getLocalizedMessage();
					}
				}
				Toast.makeText(getActivity(), "获取数据失败!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		// TODO Auto-generated method stub
		if (null != list && null != mAdapter)
		{
			mAdapter.notifyDataSetInvalidated();
		}
		switch (checkedId)
		{
		case R.id.rb_now:
			mGroup = mGroupType[0];
			mPage = mPageNew;
			mLivePostsList = mNew;
			break;
		case R.id.rb_hot:
			mGroup = mGroupType[1];
			mPage = mPageHot;
			mLivePostsList = mHot;
			break;
		case R.id.rb_all:
			mGroup = mGroupType[2];
			mPage = mPageAll;
			mLivePostsList = mAll;
			break;

		default:
			break;
		}
		Log.i(TAG, "切换显示栏目！");
		list.setSelection(0);
		showData();
	}

	private void parseResult(String result)
	{
		LiveBean data = mGson.fromJson(result, LiveBean.class);
		data.initPage();
		mPage.setPageSize(data.getPageSize());
		mPage.setAllCount(data.getAllCount());
		mPage.setPage(data.getPage());
		mPage.setPageCount(data.getPageCount());
		// mPage.initPage();

		if (null != data.getData() && !data.getData().isEmpty() && 0 != data.getPage())
		{
			if (1 == mPage.getPage())
			{
				mLivePostsList.clear();
			}
			for (Post curPost : data.getData())
			{
				curPost.setPostType(Post.TYPE_LIVE);
				mLivePostsList.add(curPost);
			}
		} else
		{
			if (0 == mPage.getPageSize())
			{
				mAdapter.notifyDataSetInvalidated();
				Log.e(TAG, "没有获取到内容");
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		if (0 == position)
		{
			mLiveType = null;
		} else
		{
			mLiveType = liveType[position];
		}
		mPage.setPage(0);
		mLivePostsList.clear();
		loadData();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// TODO Auto-generated method stub

	}

	private void onLoad()
	{
		list.stopLoadMore();
		list.stopRefresh();
		cancelProgressDialog(false);
	}

	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		onLoad();
		mPage.setPage(0);
		loadData();
	}

	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		onLoad();
		loadData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		Post post = (Post) mAdapter.getItem(position - 1);
		Intent intent = new Intent(getActivity(), PostActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(getString(R.string.tag_post), post);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
