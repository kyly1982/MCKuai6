package com.mckuai.imc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.mckuai.imc.R;
import com.mckuai.imc.adapter.LeadAdapter;

import java.util.ArrayList;

public class LeadActivity extends Activity implements OnClickListener
{
	//private int[] ids = { R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher };
	private ArrayList<ImageView> guides = new ArrayList<ImageView>(3);
	private ViewPager pager;
	private TextView tv_hint;
	private Button btn_Next;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lead);
		tv_hint = (TextView) findViewById(R.id.tv_hint);
		btn_Next = (Button) findViewById(R.id.btn_showNext);
		btn_Next.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		showLead();
	}

	private void showLead()
	{
		pager = (ViewPager) findViewById(R.id.vp_pager);
		final ArrayList<ImageView> imageViews = new ArrayList<ImageView>(5);
		for (int i = 0; i < 4; i++)
		{
			ImageView imageView = new ImageView(this);
			ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.FIT_XY);
			if (0 == i)
			{
				imageView.setImageDrawable(getResources().getDrawable(R.mipmap.background_lead_1));
			} else if (1 == i)
			{
				imageView.setImageDrawable(getResources().getDrawable(R.mipmap.background_lead_2));
			} else if (2 == i)
			{
				imageView.setImageDrawable(getResources().getDrawable(R.mipmap.background_lead_3));
			} else
			{
				imageView.setImageDrawable(getResources().getDrawable(R.mipmap.background_lead_4));
			}

			imageViews.add(imageView);
		}
		LeadAdapter adapter = new LeadAdapter(imageViews);
		pager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		pager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				// TODO Auto-generated method stub
				if (arg0 == imageViews.size() - 1)
				{
					// handler.sendMessageDelayed(handler.obtainMessage(),
					// 1500);
					btn_Next.setVisibility(View.VISIBLE);
					tv_hint.setVisibility(View.GONE);
				} else
				{
					btn_Next.setVisibility(View.GONE);
					tv_hint.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub

			}
		});
	}

	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			Intent intent = new Intent(LeadActivity.this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		};
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if (v.getId() == btn_Next.getId())
		{
			Intent intent = new Intent(LeadActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		}
	}
}
