/**
 * 
 */
package com.mckuai.imc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.imc.R;
import com.mckuai.imc.activity.MyApplication;
import com.mckuai.imc.activity.PostActivity;
import com.mckuai.imc.adapter.ForumAdapter;
import com.mckuai.imc.adapter.ForumAdapter.OnItemClickListener;
import com.mckuai.imc.adapter.PostAdapter;
import com.mckuai.imc.bean.ForumBean;
import com.mckuai.imc.bean.ForumInfo;
import com.mckuai.imc.bean.PageInfo;
import com.mckuai.imc.bean.Post;
import com.mckuai.imc.bean.PostBean;
import com.mckuai.imc.widget.XListView;
import com.mckuai.imc.widget.XListView.IXListViewListener;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class Forums extends BaseFragment implements OnItemClickListener, OnCheckedChangeListener, IXListViewListener,
		AdapterView.OnItemClickListener
{

	private ArrayList<ForumInfo> mForums;
	private PageInfo mCurPageInfo = new PageInfo();
	private ArrayList<Post> mPosts;
	private String[] listGroupType = { "lastChangeTime", "isJing", "isDing" };
	private String curGroupType = listGroupType[0];
	private XListView mPostListView;
	//private ImageButton publishPost;
	private PostAdapter mPostAdapter;
	private UltimateRecyclerView mForumsListView;
	private ForumAdapter mForumAdapter;
	private LinearLayoutManager mLayoutManager;
	private MyApplication application = MyApplication.getInstance();
	private AsyncHttpClient mClient = application.getClient();
	private String TAG = "Forums";
	private View view;
	private Gson mGson = new Gson();
	private boolean isLoading = false;
	private boolean isReadyToShow = false;
	private ForumInfo curForum;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		if (view == null)
		{
			this.view = inflater.inflate(R.layout.fragment_forums, container, false);
		}
		this.isReadyToShow = true;
		setTitle("社区");
		return view;
	}

	private void initView()
	{
		if (null == view || null != mPostListView)
		{
			return;
		}
		((RadioGroup) view.findViewById(R.id.rg_indicator)).setOnCheckedChangeListener(this);
//		publishPost.setOnClickListener(this);
		mPostListView = (XListView) view.findViewById(R.id.lv_postList);
		mPostListView.setPullLoadEnable(false);
		mPostListView.setPullRefreshEnable(true);
		mPostListView.setXListViewListener(this);
		mPostListView.setOnItemClickListener(this);
		mPostListView.setEmptyView(getActivity().findViewById(R.id.empty));
		mForumsListView = (UltimateRecyclerView) view.findViewById(R.id.rv_forums);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mForumsListView.setLayoutManager(mLayoutManager);
	}

	private void showForums()
	{
		if (null != mForums && 0 < mForums.size())
		{
//			publishPost.setVisibility(View.VISIBLE);
			if (null == mForumAdapter)
			{
				mForumAdapter = new ForumAdapter(mForums);
				mForumAdapter.setOnItemClickListener(this);
				mForumsListView.setAdapter(mForumAdapter);
			} else
			{
				mForumAdapter.setData(mForums);
			}

			if (curForum == null)
			{
				curForum = mForums.get(0);
				loadPostListDelay();
//				publishPost.setVisibility(View.GONE);
				showForums();
			}
		} else
		{
			if (null != MyApplication.getInstance().getForums() && !MyApplication.getInstance().getForums().isEmpty() && 10 < MyApplication.getInstance().getForums().length())
			{
				parseForumList(null, MyApplication.getInstance().getForums());
				showForums();
//				publishPost.setVisibility(View.VISIBLE);
			} else
			{
				loadForumList();
			}
		}
	}

	private void showPosts()
	{
		if (null != mPosts && !mPosts.isEmpty())
		{
			onLoad();
			if (null == mPostAdapter)
			{
				mPostAdapter = new PostAdapter(getActivity());
				mPostListView.setAdapter(mPostAdapter);
			}
			mPostAdapter.setData(mPosts);
			if (mCurPageInfo.getPage() < mCurPageInfo.getPageCount())
			{
				mPostListView.setPullLoadEnable(true);
			} else
			{
				mPostListView.setPullLoadEnable(false);
			}
//			publishPost.setVisibility(View.VISIBLE);
		}
	}

	private void loadForumList()
	{
		if (isLoading)
		{
			return;
		}
		isLoading = true;
		final String url = getString(R.string.interface_domainName) + getString(R.string.interface_forumList);
		mClient.get(url, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				String result = getData(url);
				if (null != result)
				{
					parseForumList(null, result);
					showForums();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int,
			 * org.apache.http.Header[], org.json.JSONObject)
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				isLoading = false;
				if (null != response && response.has("state"))
				{
					try
					{
						if (response.getString("state").equalsIgnoreCase("ok"))
						{
							parseForumList(url, response.toString());
							mCurPageInfo = new PageInfo();
							showForums();
							if (null == mPosts)
							{
								curForum = mForums.get(0);
								loadPostList(curForum);
							}
							cacheData(url, response.toString());
						}
					} catch (Exception e)
					{
						// TODO: handle exception
//						showNotification("获取板块信息失败,请重试!");
						Toast.makeText(getActivity(), "获取板块信息失败,请重试!", Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				isLoading = false;
				Toast.makeText(getActivity(), "获取板块信息失败,原因："+throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void loadPostList(ForumInfo forumInfo)
	{
		if (isLoading)
		{
			return;
		}
		isLoading = true;
		final String url = getString(R.string.interface_domainName) + getString(R.string.interface_postList);
		final RequestParams params = new RequestParams();
		params.put("forumId", forumInfo.getId());
		params.put("page", mCurPageInfo.getNextPage());
		params.put("type", curGroupType);
		mClient.get(url, params, new JsonHttpResponseHandler()
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
				String result = getData(url, params);
				if (null != result && 10 < result.length())
				{
					parsePostList(null, null, result);
					showPosts();
				} else
				{
					popupProgressDialog(getString(R.string.onloading_hint));
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int,
			 * org.apache.http.Header[], org.json.JSONObject)
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				isLoading = false;
				cancelProgressDialog(true);
				super.onSuccess(statusCode, headers, response);
				if (null != response && response.has("state"))
				{
					try
					{
						// parsePostList(url,response.getString("dataObject"));
						if (response.getString("state").equalsIgnoreCase("ok"))
						{
							parsePostList(url, params, response.getString("dataObject"));
							showPosts();
							return;
						}
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					
				}
				Toast.makeText(getActivity(), "获取帖子列表失败，请重试！", Toast.LENGTH_SHORT).show();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.loopj.android.http.JsonHttpResponseHandler#onFailure(int,
			 * org.apache.http.Header[], java.lang.String, java.lang.Throwable)
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				isLoading = false;
				cancelProgressDialog(false);
				super.onFailure(statusCode, headers, responseString, throwable);
//				showNotification("出错啦，原因："+ throwable.getLocalizedMessage());
				Toast.makeText(getActivity(), "出错啦，原因："+ throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void parseForumList(String url, String result)
	{
		try
		{
			ForumBean bean = mGson.fromJson(result, ForumBean.class);
			this.mForums = bean.getDataObject();
		} catch (Exception e)
		{
			// TODO: handle exception
		}

		if (null != url)
		{
			cacheData(url, result);
		}
	}

	private void parsePostList(String url, RequestParams params, String result)
	{
		PostBean bean = mGson.fromJson(result, PostBean.class);
		mCurPageInfo.setAllCount(bean.getAllCount());
		mCurPageInfo.setPage(bean.getPage());
		if (1 == mCurPageInfo.getPage())
		{
			if (null != url && null != params)
			{
				cacheData(url, params, result);
			}
			if (null != mPosts)
			{
				mPosts.clear();
			} else
			{
				mPosts = new ArrayList<Post>(20);
			}
		}
		mPosts.addAll(bean.getdata());
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
		if (isVisibleToUser && null != view)
		{
			initView();
			showForums();
		}
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		initView();
		if (getUserVisibleHint() && null != view)
		{
			if (isReadyToShow)
			{
				showForums();
			} else
			{
				isReadyToShow = true;
			}
		}
	}
	

	@Override
	public void onItemClick(ForumInfo forumInfo)
	{
		// TODO Auto-generated method stub
		mCurPageInfo.setPage(0);
		mForumAdapter.notifyDataSetChanged();
		loadPostList(forumInfo);
		this.curForum = forumInfo;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		// TODO Auto-generated method stub
		switch (checkedId)
		{
		case R.id.rb_lastPost:
			curGroupType = listGroupType[0];
			showPosts();
			break;
		case R.id.rb_essencePost:
			curGroupType = listGroupType[1];
			break;
		case R.id.rb_topPost:
			curGroupType = listGroupType[2];
			break;

		default:
			break;
		}
		mCurPageInfo.setPage(0);
		mPostListView.setSelection(0);
		loadPostList(curForum);
	}

	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		onLoadPost(true);
	}

	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		onLoadPost(false);
	}

	private void onLoadPost(boolean isRefresh)
	{
		onLoad();
		if (isRefresh)
		{
			mCurPageInfo.setPage(0);
		}
		loadPostList(curForum);
	}

	private void onLoad()
	{
		mPostListView.stopLoadMore();
		mPostListView.stopRefresh();
	}

	private void loadPostListDelay()
	{
		mHandler.sendMessage(mHandler.obtainMessage(9945));
	}

	Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case 9945:
				mHandler.sendMessageDelayed(mHandler.obtainMessage(5543), 200);
				break;
			case 5543:
				loadPostList(curForum);
				break;
			default:
				break;
			}
		};
	};


	public void onPause()
	{
		super.onPause();
		isReadyToShow = true;
	};

	public void onStop()
	{
		this.isReadyToShow = false;
		super.onStop();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		if (0 < position && mPosts.size() >= position)
		{
			position--;
			Post post = mPosts.get(position);
			Intent intent = new Intent(getActivity(), PostActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(getString(R.string.tag_post), post);
			intent.putExtras(bundle);
			startActivity(intent);
		}

	}

    public ArrayList<ForumInfo> getmForums() {
        return mForums;
    }
}
