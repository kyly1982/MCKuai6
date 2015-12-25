/**
 * 
 */
package com.mckuai.imc.bean;

/**
 * @author kyly
 * 
 */
public class RCUser
{
	private String id;
	private String name;
	private String portraitUri;
	
	/**
	 * 
	 */
	public RCUser(String id,String name,String portrait)
	{
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.portraitUri = portrait;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPortraitUri()
	{
		return portraitUri;
	}

	public void setPortraitUri(String portraitUri)
	{
		this.portraitUri = portraitUri;
	}
}
