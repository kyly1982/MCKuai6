/**
 * 
 */
package com.mckuai.imc.bean;

import java.io.Serializable;

/**
 * @author kyly
 * 
 */
public class PageInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5861226865491533602L;
	private int allCount;// 总条目数
	private int page;// 当前页
	private int pageSize = 20;// 每页条数
	private int pageCount;// 总页数
	private boolean EOF = false;// 是否已经到末尾

	public PageInfo()
	{

	}

	public PageInfo(int allCount, int page, int pageSize)
	{
		// TODO Auto-generated constructor stub
		this.allCount = allCount;
		this.page = page;
		this.pageSize = pageSize;
		initPage();
	}

	public int getAllCount()
	{
		return allCount;
	}

	public void setAllCount(int allCount)
	{
		this.allCount = allCount;
		if (0 != pageSize)
		{
			pageCount = allCount / pageSize;
			if (0 != allCount % pageSize)
			{
				pageCount++;
			}
		}
	}

	public int getPage()
	{
		return page;
	}

	public void setPage(int page)
	{
		this.page = page;
		if (0 != pageSize)
		{
			pageCount = allCount / pageSize;
			if (0 != allCount % pageSize)
			{
				pageCount++;
			}
		}
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public int getNextPage()
	{
		return page + 1;
	}

	public int getPageCount()
	{
		initPage();
		return pageCount;
	}

	public void setPageCount(int pageCount)
	{
		this.pageCount = pageCount;
	}

	public void initPage()
	{
		if (pageSize > 0)
		{
			int curpage = allCount / pageSize;
			this.pageCount = allCount % pageSize == 0 ? curpage : curpage + 1;
			this.EOF = (this.page >= this.pageCount);
		} else
		{
			this.page = 1;
			this.pageCount = 1;
			this.EOF = true;
		}
	}

	public boolean isEOF()
	{
		initPage();
		return EOF;
	}

	public void setEOF(boolean eOF)
	{
		EOF = eOF;
	}

	public void resetPage()
	{
		this.page = 0;
		this.pageCount = 1;
		this.EOF = false;
	}
}
