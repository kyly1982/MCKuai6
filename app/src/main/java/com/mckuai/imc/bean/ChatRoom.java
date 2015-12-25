package com.mckuai.imc.bean;

import java.io.Serializable;

public class ChatRoom implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6545458986280051082L;

	private int id;
	private int onLine;
	private String name;
	private String lastSpeak;
	private String headImg;
	private String icon;
	private String insertTime;
	private String endTime;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getOnLine()
	{
		return onLine;
	}

	public void setOnLine(int onLine)
	{
		this.onLine = onLine;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLastSpeak()
	{
		return lastSpeak;
	}

	public void setLastSpeak(String lastSpeak)
	{
		this.lastSpeak = lastSpeak;
	}

	public String getHeadImg()
	{
		return headImg;
	}

	public void setHeadImg(String headImg)
	{
		this.headImg = headImg;
	}

	public String getIcon()
	{
		return icon;
	}

	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	public String getInsertTime()
	{
		return insertTime;
	}

	public void setInsertTime(String insertTime)
	{
		this.insertTime = insertTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
}
