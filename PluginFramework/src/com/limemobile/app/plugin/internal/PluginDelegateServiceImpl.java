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
package com.limemobile.app.plugin.internal;

import java.lang.reflect.Constructor;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.text.TextUtils;

import com.limemobile.app.plugin.IPluginService;

public class PluginDelegateServiceImpl {
	protected String mPluginClientServiceClass;
	protected String mPluginClientPackageName;

	protected PluginClientInfo mPluginClient;
	protected PluginClientManager mPluginManager;

	protected AssetManager mAssetManager;
	protected Resources mResources;
	protected Theme mTheme;

	protected ServiceInfo mServiceInfo;
	protected Service mPluginHostService;
	protected IPluginService mDelegatedService;

	public PluginDelegateServiceImpl(Service service) {
		mPluginHostService = service;
	}

	public void onCreate(Intent intent) {
		mPluginClientPackageName = intent
				.getStringExtra(PluginClientManager.INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME);
		mPluginClientServiceClass = intent
				.getStringExtra(PluginClientManager.INTENT_EXTRA_PLUGIN_CLIENT_SERVICE_CLASS);

		mPluginManager = PluginClientManager.sharedInstance(mPluginHostService);
		mPluginClient = mPluginManager
				.getPluginClient(mPluginClientPackageName);
		mAssetManager = mPluginClient.mAssetManager;
		mResources = mPluginClient.mResources;

		initializeServiceInfo();
		handleServiceInfo();
		launchTargetService();
	}

	private void initializeServiceInfo() {
		PackageInfo packageInfo = mPluginClient.mClientPackageInfo;
		if ((packageInfo.services != null) && (packageInfo.services.length > 0)) {
			if (TextUtils.isEmpty(mPluginClientServiceClass)) {
				mPluginClientServiceClass = packageInfo.services[0].name;
			}
			for (ServiceInfo a : packageInfo.services) {
				if (a.name.equals(mPluginClientServiceClass)) {
					mServiceInfo = a;
				}
			}
		}
	}

	private void handleServiceInfo() {
		Theme superTheme = mPluginHostService.getTheme();
		mTheme = mResources.newTheme();
		mTheme.setTo(superTheme);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void launchTargetService() {
		try {
			Class<?> localClass = getClassLoader().loadClass(
					mPluginClientServiceClass);
			Constructor<?> localConstructor = localClass
					.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});
			if (!(instance instanceof IPluginService)) {
				throw new IllegalArgumentException();
			}
			mDelegatedService = (IPluginService) instance;
			((IPluginServiceDelegate) mPluginHostService)
					.attach(mDelegatedService);

			mDelegatedService.setDelegate(mPluginHostService, mPluginClient);

			mDelegatedService.onCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ClassLoader getClassLoader() {
		return mPluginClient.mClassLoader;
	}

	public AssetManager getAssets() {
		return mAssetManager;
	}

	public Resources getResources() {
		return mResources;
	}

	public Theme getTheme() {
		return mTheme;
	}

	public IPluginService getRemoteService() {
		return mDelegatedService;
	}
}
