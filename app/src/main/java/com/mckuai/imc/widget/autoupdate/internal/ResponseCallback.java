package com.mckuai.imc.widget.autoupdate.internal;


import com.mckuai.imc.widget.autoupdate.Version;

public interface ResponseCallback {
	void onFoundLatestVersion(Version version);
	void onCurrentIsLatest();
}
