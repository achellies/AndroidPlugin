/*
 * Copyright (C) 2014 achellies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.limemobile.app.plugin;

import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.IBinder;

import com.limemobile.app.plugin.internal.IPluginServiceDelegate;
import com.limemobile.app.plugin.internal.PluginClientManager;
import com.limemobile.app.plugin.internal.PluginDelegateServiceImpl;

public class PluginHostDelegateService extends Service implements
		IPluginServiceDelegate {
	protected IPluginService mDelegatedService;
	protected PluginDelegateServiceImpl mDelegateImpl = new PluginDelegateServiceImpl(
			this);

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void attach(IPluginService delegatedService) {
		mDelegatedService = delegatedService;
	}

	@Override
	public AssetManager getAssets() {
		return mDelegateImpl.getAssets() == null ? super.getAssets()
				: mDelegateImpl.getAssets();
	}

	@Override
	public Resources getResources() {
		return mDelegateImpl.getResources() == null ? super.getResources()
				: mDelegateImpl.getResources();
	}

	@Override
	public Theme getTheme() {
		return mDelegateImpl.getTheme() == null ? super.getTheme()
				: mDelegateImpl.getTheme();
	}

	@Override
	public ClassLoader getClassLoader() {
		if (mDelegatedService == null) {
			return super.getClassLoader();
		}
		return mDelegateImpl.getClassLoader();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		if (mDelegatedService == null) {
			mDelegateImpl.onCreate(intent);
		}
		mDelegatedService.onStart(intent, startId);
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mDelegatedService == null) {
			mDelegateImpl.onCreate(intent);
		}
		mDelegatedService.onStartCommand(intent, flags, startId);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		mDelegatedService.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mDelegatedService.onConfigurationChanged(newConfig);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		mDelegatedService.onLowMemory();
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		mDelegatedService.onTrimMemory(level);
		super.onTrimMemory(level);
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (mDelegatedService == null) {
			mDelegateImpl.onCreate(intent);
		}
		return mDelegatedService.onBind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mDelegatedService.onUnbind(intent);
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		mDelegatedService.onRebind(intent);
		super.onRebind(intent);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		mDelegatedService.onTaskRemoved(rootIntent);
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public ComponentName startService(Intent service) {
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos == null || resolveInfos.isEmpty()) {
			service.setPackage(mDelegatedService.getPackageName());
		} else {
			return super.startService(service);
		}
		return PluginClientManager.sharedInstance(this).startService(this,
				service);
	}

	@Override
	public boolean stopService(Intent service) {
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos == null || resolveInfos.isEmpty()) {
			service.setPackage(mDelegatedService.getPackageName());
		} else {
			return super.stopService(service);
		}
		return PluginClientManager.sharedInstance(this).stopService(this,
				service);
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		mDelegatedService.unbindService(conn);
		super.unbindService(conn);
	}

	@Override
	public void startActivity(Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos == null || resolveInfos.isEmpty()) {
			intent.setPackage(mDelegatedService.getPackageName());
		} else {
			super.startActivity(intent);
			return;
		}
		PluginClientManager.sharedInstance(this).startActivity(this, intent);
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos == null || resolveInfos.isEmpty()) {
			intent.setPackage(mDelegatedService.getPackageName());
		} else {
			super.startActivity(intent, options);
			return;
		}
		PluginClientManager.sharedInstance(this).startActivity(this, intent,
				options);
	}
}
