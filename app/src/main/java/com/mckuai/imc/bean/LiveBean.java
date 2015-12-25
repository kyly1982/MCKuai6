package com.mckuai.imc.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class LiveBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Post> data;//直播帖子
	private int page;//当前页数
	private int pageCount;//总页数
	private int pageSize;//分页大小
	private int allCount;//总页数
	
	public void initPage(){
		if (0 != pageSize)
		{
			pageCount = allCount / pageSize;
			pageCount += 0 == allCount % pageSize ? 0 : 1;
		}
	}
	
	public ArrayList<Post> getData()
	{
		return data;
	}
	public void setData(ArrayList<Post> data)
	{
		this.data = data;
	}
	public int getPage()
	{
		return page;
	}
	public void setPage(int page)
	{
		this.page = page;
	}
	public int getPageCount()
	{
		return pageCount;
	}
	public void setPageCount(int pageCount)
	{
		this.pageCount = pageCount;
	}
	public int getPageSize()
	{
		return pageSize;
	}
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}
	public int getAllCount()
	{
		return allCount;
	}
	public void setAllCount(int allCount)
	{
		this.allCount = allCount;
	}
}
