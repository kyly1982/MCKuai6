package com.mckuai.imc.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.R;
import com.mckuai.imc.activity.MainActivity;
import com.mckuai.imc.activity.MyApplication;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class Chat extends BaseFragment
{

	private static View view;
	private String TAG = "Chat";
	private MyApplication application;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (null == application)
		{
			application = MyApplication.getInstance();
		}
		if (application.isLogin() && application.isLoginRC())
		{
			if (view != null)
			{
//				Log.e(TAG, "view已存在");
				ViewGroup parent = (ViewGroup) view.getParent();
				try
				{
					parent.removeView(view);
				} catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			try
			{
				view = inflater.inflate(R.layout.fragment_chat, container, false);
			} catch (InflateException e)
			{
			}
			return view;
		} else
		{
			view = inflater.inflate(R.layout.fragment_chat_unlogin, container, false);
			return view;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("聊天");
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            enterFragment();
        }
//		Log.e(TAG, "setUserVisibleHint");
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //enterFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private void enterFragment() {
        if (null != getChildFragmentManager()) {
            ConversationListFragment fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.fragment_friendlist);
            if (null != fragment) {
                Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                        .build();

                fragment.setUri(uri);
            }
        }
    }

}
