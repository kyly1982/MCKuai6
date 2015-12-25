/**
 * 
 */
package com.mckuai.imc.bean;

import android.graphics.drawable.Drawable;

/**
 * @author kyly
 *
 */
public class AppInfo {
	private String name="";//应用名称
	private String packageName="";//包名
	private String version="";//版本
	private int versionCode=0;//版本号
	private Drawable icon=null;//图标
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
