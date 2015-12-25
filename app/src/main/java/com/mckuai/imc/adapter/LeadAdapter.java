/**
 * 
 */
package com.mckuai.imc.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * @author kyly
 *
 */
public class LeadAdapter extends PagerAdapter
{
	
	private ArrayList<ImageView> mViews = new ArrayList<ImageView>();
	
	/**
	 * 
	 */
	public LeadAdapter(ArrayList<ImageView> views)
	{
		// TODO Auto-generated constructor stub
		this.mViews = views;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		Log.i("getCount", mViews.size()+"");
		return mViews.size();
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		// TODO Auto-generated method stub
		//return super.instantiateItem(container, position);
		container.addView(mViews.get(position));
		return mViews.get(position);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		// TODO Auto-generated method stub
		container.removeView(mViews.get(position));
	}
	
	

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
