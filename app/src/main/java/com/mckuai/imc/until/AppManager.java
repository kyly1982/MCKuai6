/**
 * 
 */
package com.mckuai.imc.until;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.mckuai.imc.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author kyly
 *	检查安装、检查更新、启动应用
 */
public class AppManager {
	private Context mContext;
	/**
	 * 
	 */
	public AppManager(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	public ArrayList<AppInfo> getInstalledApp(){
		ArrayList<AppInfo> mAppList = new ArrayList<AppInfo>(10);
		List<PackageInfo>  mPackageInfoList = mContext.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < mPackageInfoList.size(); i++) {
			//添加非系统应用
			if (0 == (mPackageInfoList.get(i).applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)) {
				AppInfo tempInfo = new AppInfo();
				tempInfo.setName(mPackageInfoList.get(i).applicationInfo.loadLabel(mContext.getPackageManager()).toString());
				tempInfo.setPackageName(mPackageInfoList.get(i).packageName);
				tempInfo.setVersion(mPackageInfoList.get(i).versionName);
				tempInfo.setVersionCode(mPackageInfoList.get(i).versionCode);
				tempInfo.setIcon(mPackageInfoList.get(i).applicationInfo.loadIcon(mContext.getPackageManager()));
				mAppList.add(tempInfo);
			}
		}
		return mAppList;
	}
	
	public void installApp(String url){
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(Uri.fromFile(new File(url)), "application/vnd.android.package-archive");
		mContext.startActivity(installIntent);
	}
	
	public void installApp(Uri appUri){
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(appUri, "application/vnd.android.package-archive");
		mContext.startActivity(installIntent);
	}
	
	public void removeApp(String packageName){
		Uri PackageURI = Uri.parse("package:"+packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, PackageURI);
		mContext.startActivity(uninstallIntent);
	}
}
