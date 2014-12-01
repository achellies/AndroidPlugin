package com.limemobile.app.plugin;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;

import com.limemobile.app.plugin.internal.PluginClientInfo;

public abstract class PluginClientService extends Service implements
		IPluginService {
	protected Service mContext;

	protected Service mProxyService;

	protected PluginClientInfo mPluginPackage;

	@Override
	public void setDelegate(Service pluginHostService,
			PluginClientInfo pluginPackage) {
		mContext = pluginHostService;
		mProxyService = pluginHostService;
		mPluginPackage = pluginPackage;
	}

	@Override
	public void onCreate() {
		if (mProxyService == null) {
			mContext = this;
			super.onCreate();
		}
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		if (mProxyService == null) {
			super.onStart(intent, startId);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mProxyService == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		return 0;
	}

	@Override
	public void onDestroy() {
		if (mProxyService == null) {
			super.onDestroy();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mProxyService == null) {
			super.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public void onLowMemory() {
		if (mProxyService == null) {
			super.onLowMemory();
		}
	}

	@Override
	public void onTrimMemory(int level) {
		if (mProxyService == null) {
			super.onTrimMemory(level);
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (mProxyService == null) {
			return super.onUnbind(intent);
		}
		return true;
	}

	@Override
	public void onRebind(Intent intent) {
		if (mProxyService == null) {
			super.onRebind(intent);
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		if (mProxyService == null) {
			super.onTaskRemoved(rootIntent);
		}
	}

}
