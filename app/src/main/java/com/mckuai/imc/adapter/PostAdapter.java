package com.mckuai.imc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mckuai.imc.R;
import com.mckuai.imc.activity.MyApplication;
import com.mckuai.imc.activity.UserCenter;
import com.mckuai.imc.bean.ChatRoom;
import com.mckuai.imc.bean.Post;
import com.mckuai.imc.bean.User;
import com.mckuai.imc.widget.Anticlock;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

//import com.mckuai.imc.UserCenter;

public class PostAdapter extends BaseAdapter implements View.OnClickListener
{
	private ArrayList<Post> mPostList = new ArrayList<Post>(10);
	private ArrayList<Post> mLiveList;
	private Context mContext;
	private LayoutInflater mInflater;
	private boolean isRecommend = false;
	private ChatRoom mChatRoom;
	private ImageLoader mLoader;
	private DisplayImageOptions normal;
	private DisplayImageOptions circle;

	private static final String TAG = "PostAdapter";

	private static final int TYPE_CHATROOM = 0;
	private static final int TYPE_LIVE = 1;
	private static final int TYPE_POST = 2;
	private static final int TYPE_POST_RECOMMEND = 3;
	private static final int TYPE_HEADER_LIVE = 4;
	private static final int TYPE_HEADER_POST = 5;

	public PostAdapter(Context context)
	{
		this(context, null);
	}

	public PostAdapter(Context context, ChatRoom chatRoom, ArrayList<Post> lives, ArrayList<Post> post)
	{
		init(context);
		this.isRecommend = true;
		this.mChatRoom = chatRoom;
		this.mLiveList = lives;
		this.mPostList = post;
		for (Post curLive : mLiveList)
		{
			curLive.setPostType(Post.TYPE_LIVE);
		}
	}

	public PostAdapter(Context context, ArrayList<Post> data)
	{
		this.isRecommend = false;
		init(context);
		this.mPostList = data;
	}

	public void refresh()
	{
		this.notifyDataSetChanged();
	}

	protected void init(Context context)
	{
		this.mContext = context;
		this.mLoader = ImageLoader.getInstance();
		this.mInflater = (LayoutInflater) context.getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.normal = MyApplication.getInstance().getNormalOptions();
		this.circle = MyApplication.getInstance().getCircleOptions();
	}

	public void setData(ChatRoom chatRoom, ArrayList<Post> livelist, ArrayList<Post> postlist)
	{
		this.mChatRoom = chatRoom;
		this.mLiveList = livelist;
		this.mPostList = postlist;
		this.isRecommend = true;
		notifyDataSetChanged();
	}

	public void setData(ArrayList<Post> data)
	{
		this.isRecommend = false;
		if (null != data)
		{
			this.mPostList = data;
			notifyDataSetChanged();
		} else
		{
			notifyDataSetInvalidated();
		}
	}

	@Override
	public int getCount()
	{
		if (isRecommend)
		{
			return ((null == mPostList ? 0 : (mPostList.size() + 1)) + (null == mChatRoom ? 0 : 1) + (null == mLiveList ? 0
					: (mLiveList.size() + 1)));
		} else
		{
			return (null == mPostList ? 0 : mPostList.size());
		}
	}

	@Override
	public int getViewTypeCount()
	{
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public int getItemViewType(int position)
	{
		// TODO Auto-generated method stub
		if (isRecommend)
		{
			if (0 == position)
			{
				return TYPE_CHATROOM;
			} else if (1 == position)
			{
				return TYPE_HEADER_LIVE;
			} else if (1 < position && position < 2 + mLiveList.size())
			{
				return TYPE_LIVE;
			} else if (position == mLiveList.size() + 2)
			{
				return TYPE_HEADER_POST;
			} else
			{
				return TYPE_POST_RECOMMEND;
			}
		} else
		{
			switch (mPostList.get(position).getPostType())
			{
			case Post.TYPE_LIVE:
				return TYPE_LIVE;

			default:
				return TYPE_POST;
			}
		}
	}

	@Override
	public Object getItem(int position)
	{
		if (isRecommend)
		{
			if (0 == position)
			{
				return mChatRoom;
			} else if (1 < position && position < (mLiveList.size() + 2))
			{
				return mLiveList.get(position - 2);
			} else if (position > (2 + mLiveList.size()))
			{
				return mPostList.get(position - mLiveList.size() - 3);
			}
		} else
		{
			return mPostList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		// Log.e(TAG, "getView");
		switch (getItemViewType(position))
		{
		case TYPE_CHATROOM:
			// Log.e(TAG, "聊天室");
			return handleChatRoom(position, convertView, parent);
		case TYPE_LIVE:
			return handleLive(position, convertView, parent);
		case TYPE_POST_RECOMMEND:
			return handlePost(position, convertView, parent);

		case TYPE_POST:
			return handleNormalPost(position, convertView, parent);
		case TYPE_HEADER_LIVE:
			return handleLiveHeader(convertView, parent);
		case TYPE_HEADER_POST:
			return handlePostHeader(convertView, parent);

		default:
			Log.e(TAG, "type=" + getItemViewType(position));
			return null;
		}
	}

	/**
	 * handleChatRoom:处理聊天室类的条目<br>
	 * 
	 * @param position
	 * @param view
	 * @param parent
	 * @return
	 */
	protected View handleChatRoom(int position, View view, ViewGroup parent)
	{
		if (null != mChatRoom)
		{
			ChatRoom_ViewHolder holder;
			if (null == view)
			{
				view = mInflater.inflate(R.layout.item_chatroom, parent, false);
				holder = new ChatRoom_ViewHolder();
				holder.cover = (ImageView) view.findViewById(R.id.iv_postCover);
				holder.lastUser = (ImageView) view.findViewById(R.id.civ_lastUser);
				holder.lastMessage = (TextView) view.findViewById(R.id.tv_lastMessage);
				holder.theme = (TextView) view.findViewById(R.id.tv_postTitle);
				holder.time = (Anticlock) view.findViewById(R.id.antiClock);
				view.setTag(holder);
			} else
			{
				holder = (ChatRoom_ViewHolder) view.getTag();
			}
			// 显示内容
			holder.lastMessage.setText(mChatRoom.getLastSpeak() + "");
			holder.theme.setText(mChatRoom.getName() + "");
			if (null != mChatRoom.getIcon() && 10 < mChatRoom.getIcon().length())
			{
				mLoader.displayImage(mChatRoom.getIcon() + "", holder.cover, normal);
			}
			if (null != mChatRoom.getHeadImg() && 10 < mChatRoom.getHeadImg().length())
			{
				mLoader.displayImage(mChatRoom.getHeadImg() + "", holder.lastUser, circle);
			}
		}
		return view;
	}

	/**
	 * handleLive:直播<br>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 * 
	 * @param position
	 * @param view
	 * @param parent
	 * @return
	 */
	protected View handleLive(int position, View view, ViewGroup parent)
	{
		Log.i(TAG, "handleLive");
		if (null != mPostList && null != getItem(position))
		{
			Post post = (Post) getItem(position);
			Live_ViewHolder holder;
			if (null == view)
			{
				view = mInflater.inflate(R.layout.item_live, parent, false);
				holder = new Live_ViewHolder();
				holder.cover = (ImageView) view.findViewById(R.id.iv_postCover);
				holder.owner_cover = (ImageView) view.findViewById(R.id.civ_owner);
				holder.owner_name = (TextView) view.findViewById(R.id.tv_postOwner_bottom);
				holder.reply = (TextView) view.findViewById(R.id.tv_reply);
				holder.status = (TextView) view.findViewById(R.id.tv_postStatus);
				holder.title = (TextView) view.findViewById(R.id.tv_postTitle);
				holder.type = (TextView) view.findViewById(R.id.tv_type);
				view.setTag(holder);
			} else
			{
				holder = (Live_ViewHolder) view.getTag();
			}
			// 显示内容

			if (null != post.getMobilePic() && 10 < post.getMobilePic().length())
			{
				mLoader.displayImage(post.getMobilePic() + "", holder.cover, normal);
			}
			if (null != post.getHeadImg() && 10 < post.getHeadImg().length())
			{
				mLoader.displayImage(post.getHeadImg() + "", holder.owner_cover, circle);
			}
			holder.owner_name.setText(post.getUserName() + "");
			holder.reply.setText(post.getReplyNum() + "");
			holder.type.setText(post.getTalkType());// 直播类型
			holder.title.setText(post.getTalkTitle() + "");
			// 设置直播状态
			switch (post.getIsLive())
			{
			case 1:
				holder.status.setText(mContext.getString(R.string.live_now));
				break;

			default:
				holder.status.setText(mContext.getString(R.string.live_finish));
				break;
			}
			// 设置点击用户跳转
			holder.owner_cover.setTag(R.id.key_USERID, post.getUserId());
			holder.owner_name.setTag(R.id.key_USERID, post.getUserId());
			holder.owner_cover.setOnClickListener(this);
			holder.owner_name.setOnClickListener(this);
		}
		return view;
	}

	/**
	 * handlePost:帖子<br>
	 * 如果是在社区中，则上边的owner显示所属板块,下方owner显示用户名字 如果不是在社区中，上方owner不显示，下方owner显示所属版块
	 * 
	 * @param position
	 * @param view
	 * @param parent
	 * @return
	 */
	protected View handlePost(int position, View view, ViewGroup parent)
	{
		if (null != mPostList && null != getItem(position))
		{
			Post_ViewHolder holder;
			Post post = (Post) getItem(position);
			if (null == view)
			{
				view = mInflater.inflate(R.layout.item_post_recommend, parent, false);
				holder = new Post_ViewHolder();
				holder.cover = (ImageView) view.findViewById(R.id.iv_postCover);
				holder.owner_bottom = (TextView) view.findViewById(R.id.tv_postOwner_bottom);
				holder.owner_bottom_cover = (ImageView) view.findViewById(R.id.civ_postOwner_bottom);
				holder.typeTop = (ImageView) view.findViewById(R.id.tv_typeTop);
				holder.typeEssence = (ImageView) view.findViewById(R.id.tv_typeEssence);
				holder.title = (TextView) view.findViewById(R.id.tv_postTitle);
				holder.replyCount = (TextView) view.findViewById(R.id.v_postReply);
				holder.replyTime = (TextView) view.findViewById(R.id.tv_postrepayTime);
				view.setTag(holder);
			} else
			{
				holder = (Post_ViewHolder) view.getTag();
			}
			mLoader.displayImage(post.getMobilePic() + "", holder.cover, normal);// 封面
			holder.title.setText(post.getTalkTitle() + "");
			holder.replyCount.setText(post.getReplyNum() + "");
			holder.replyTime.setText(post.getLastReplyTime());
			if (post.isTop())
			{
				holder.typeTop.setVisibility(View.VISIBLE);
			} else
			{
				holder.typeTop.setVisibility(View.GONE);
			}
			if (post.isEssence())
			{
				holder.typeEssence.setVisibility(View.VISIBLE);
			} else
			{
				holder.typeEssence.setVisibility(View.GONE);
			}
			if (null != post.getUserName())
			{
				holder.owner_bottom.setText(post.getUserName() + "");
				if (null != post.getHeadImg() && 10 < post.getHeadImg().length())
				{
					mLoader.displayImage(post.getHeadImg(), holder.owner_bottom_cover, circle);
				}
				// 设置用户头像和名字点击跳转到其个人中心
				holder.owner_bottom.setTag(R.id.key_USERID, post.getUserId());
				holder.owner_bottom_cover.setTag(R.id.key_USERID, post.getUserId());
				holder.owner_bottom.setOnClickListener(this);
				holder.owner_bottom_cover.setOnClickListener(this);
			} else
			{
				holder.owner_bottom.setText(post.getForumName());
				if (null != post.getIcon() && 10 < post.getIcon().length())
				{
					mLoader.displayImage(post.getIcon(), holder.owner_bottom_cover, circle);
				}
			}

		}
		return view;
	}

	private View handleNormalPost(int position, View view, ViewGroup parent)
	{
		if (null != mPostList && -1 < position && position < mPostList.size())
		{
			Post_ViewHolder holder;
			Post post = mPostList.get(position);
			if (null == view)
			{
				view = mInflater.inflate(R.layout.item_post_normal, parent, false);
				holder = new Post_ViewHolder();
				holder.owner_bottom = (TextView) view.findViewById(R.id.tv_postOwner_bottom);
				holder.owner_bottom_cover = (ImageView) view.findViewById(R.id.civ_postOwner_bottom);
				holder.typeTop = (ImageView) view.findViewById(R.id.iv_typeTop);
				holder.typeEssence = (ImageView) view.findViewById(R.id.iv_typeEssence);
				holder.title = (TextView) view.findViewById(R.id.tv_postTitle);
				holder.replyCount = (TextView) view.findViewById(R.id.tv_postReply);
				holder.replyTime = (TextView) view.findViewById(R.id.tv_postrepayTime);
				view.setTag(holder);
			} else
			{
				holder = (Post_ViewHolder) view.getTag();
			}
			holder.title.setText(post.getTalkTitle() + "");
			holder.replyCount.setText(post.getReplyNum() + "");
			holder.replyTime.setText(post.getLastReplyTime());
			if (post.isTop())
			{
				holder.typeTop.setVisibility(View.VISIBLE);
			} else
			{
				holder.typeTop.setVisibility(View.GONE);
			}
			if (post.isEssence())
			{
				holder.typeEssence.setVisibility(View.VISIBLE);
			} else
			{
				holder.typeEssence.setVisibility(View.GONE);
			}
			if (null != post.getUserName())
			{
				holder.owner_bottom.setText(post.getUserName() + "");
				if (null != post.getHeadImg() && 10 < post.getHeadImg().length())
				{
					mLoader.displayImage(post.getHeadImg(), holder.owner_bottom_cover, circle);
				}
				// 设置用户头像和名字点击跳转到其个人中心
				holder.owner_bottom.setTag(R.id.key_USERID, post.getUserId());
				holder.owner_bottom_cover.setTag(R.id.key_USERID, post.getUserId());
				holder.owner_bottom.setOnClickListener(this);
				holder.owner_bottom_cover.setOnClickListener(this);
			} else
			{
				holder.owner_bottom.setText(post.getForumName());
				if (null != post.getIcon() && 10 < post.getIcon().length())
				{
					mLoader.displayImage(post.getIcon(), holder.owner_bottom_cover, circle);
				}
			}
		}
		return view;
	}

	private View handleLiveHeader(View view, ViewGroup parent)
	{
		if (null == view)
		{
			view = mInflater.inflate(R.layout.item_live_header, parent, false);
		}
		return view;
	}

	private View handlePostHeader(View view, ViewGroup parent)
	{
		if (null == view)
		{
			view = mInflater.inflate(R.layout.item_post_header, parent, false);
		}
		return view;
	}

	class ChatRoom_ViewHolder
	{
		Anticlock time;
		ImageView cover;
		ImageView lastUser;
		TextView theme;
		TextView lastMessage;
	}

	class Live_ViewHolder
	{
		ImageView cover;
		ImageView owner_cover;
		TextView title;
		TextView reply;
		TextView owner_name;
		TextView type;
		TextView status;
	}

	class Post_ViewHolder
	{
		ImageView cover;
		ImageView owner_bottom_cover;
		TextView owner_bottom;
		TextView title;
		ImageView typeTop;
		ImageView typeEssence;
		TextView replyTime;
		TextView replyCount;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		int userId = 0, forumId = 0;
		try
		{
			userId = (Integer) v.getTag(R.id.key_USERID);
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		if (0 != userId)
		{
			Intent intent = new Intent(mContext, UserCenter.class);
			User user = new User();
			user.setId(userId);
			Bundle bundle = new Bundle();
			bundle.putSerializable(mContext.getString(R.string.user), user);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		} else
		{
			Toast.makeText(mContext, "版块ID" + forumId, Toast.LENGTH_SHORT).show();
		}
	}

}
