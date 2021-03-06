package com.mckuai.imc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mckuai.imc.fragment.BaseFragment;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {
	
	List<BaseFragment> list;

	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public FragmentAdapter(FragmentManager fm,List<BaseFragment> list) {
		super(fm);
		this.list=list;
	}
	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}
}
