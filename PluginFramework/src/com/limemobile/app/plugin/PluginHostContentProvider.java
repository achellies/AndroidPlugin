package com.limemobile.app.plugin;

import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.Configuration;

import com.limemobile.app.plugin.internal.PluginClientInfo;

public abstract class PluginHostContentProvider extends ContentProvider
		implements IPluginContentProvider {
	protected PluginClientInfo mPluginPackage;

	protected ContentProvider mProxyContentProvider;

	@Override
	public void setDelegate(ContentProvider pluginHostContentProvider,
			PluginClientInfo pluginPackage) {
		this.mProxyContentProvider = pluginHostContentProvider;
		this.mPluginPackage = pluginPackage;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mProxyContentProvider == null) {
			super.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public void onLowMemory() {
		if (mProxyContentProvider == null) {
			super.onLowMemory();
		}
	}

	@Override
	public void attachInfo(Context context, ProviderInfo info) {
		if (mProxyContentProvider == null) {
			super.attachInfo(context, info);
		}
	}

	@Override
	public void shutdown() {
		if (mProxyContentProvider == null) {
			super.shutdown();
		}
	}

}