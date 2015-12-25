/**
 * 
 */
package com.mckuai.imc.bean;

import java.util.ArrayList;

/**
 * @author kyly
 *
 */
public class RecommendBean
{
	private ChatRoom chat;
	private ArrayList<Post> live= new ArrayList<Post>(3);
	private ArrayList<Post> talk= new ArrayList<Post>(10);
	
	public ChatRoom getChat()
	{
		return chat;
	}
	public void setChat(ChatRoom chat)
	{
		this.chat = chat;
	}
	public ArrayList<Post> getLive()
	{
		return live;
	}
	public void setLive(ArrayList<Post> live)
	{
		this.live = live;
	}
	public ArrayList<Post> getTalk()
	{
		return talk;
	}
	public void setTalk(ArrayList<Post> talk)
	{
		this.talk = talk;
	}
	
	public void resetPostType(){
		for (Post mPost : live)
		{
			mPost.setPostType(Post.TYPE_LIVE);
		}
	}
	
	public ArrayList<Post> getAllPostList(){
		ArrayList<Post> temp = (ArrayList<Post>) live.clone();
		temp.addAll(talk);
		return temp;
	}
}
