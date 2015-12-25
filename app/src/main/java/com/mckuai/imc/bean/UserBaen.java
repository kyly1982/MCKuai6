package com.mckuai.imc.bean;

import java.util.ArrayList;

public class UserBaen
{
	private int allCount = 0;
	private ArrayList<User> data = new ArrayList<User>();
	private int page = 1;
	private int pageCount = 0;
	private int pageSize = 20;

	public int getallCount()
	{
		return allCount;
	}

	public void setallCount(int allCount)
	{
		this.allCount = allCount;
	}

	public ArrayList<User> getdata()
	{
		return data;
	}

	public void setLive(ArrayList<User> data)
	{
		this.data = data;
	}

	public int getpage()
	{
		return page;
	}
	
	public int getNextPage(){
		return page + 1;
	}

	public void setpage(int page)
	{
		this.page = page;
	}

	public int getpageCount()
	{
		return pageCount;
	}

	public void setpageCount(int pageCount)
	{
		this.pageCount = pageCount;
	}

	public int getpageSize()
	{
		return pageSize;
	}

	public void setpageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

}