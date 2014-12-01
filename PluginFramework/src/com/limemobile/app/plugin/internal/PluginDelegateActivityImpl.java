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
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.limemobile.app.plugin.IPluginActivity;

public class PluginDelegateActivityImpl {
	protected String mPluginClientActivityClass;
	protected String mPluginClientPackageName;

	protected PluginClientInfo mPluginClient;
	protected PluginClientManager mPluginManager;

	protected AssetManager mAssetManager;
	protected Resources mResources;
	protected Theme mTheme;

	protected ActivityInfo mActivityInfo;
	protected Activity mPluginHostActivity;
	protected IPluginActivity mDelegatedActivity;

	public PluginDelegateActivityImpl(Activity activity) {
		mPluginHostActivity = activity;
	}

	public void onCreate(Intent intent) {
		mPluginClientPackageName = intent
				.getStringExtra(PluginClientManager.INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME);
		mPluginClientActivityClass = intent
				.getStringExtra(PluginClientManager.INTENT_EXTRA_PLUGIN_CLIENT_ACTIVITY_CLASS);

		mPluginManager = PluginClientManager
				.sharedInstance(mPluginHostActivity);
		mPluginClient = mPluginManager
				.getPluginClient(mPluginClientPackageName);
		mAssetManager = mPluginClient.mAssetManager;
		mResources = mPluginClient.mResources;

		initializeActivityInfo();
		handleActivityInfo();
		launchTargetActivity();
	}

	private void initializeActivityInfo() {
		PackageInfo packageInfo = mPluginClient.mClientPackageInfo;
		if ((packageInfo.activities != null)
				&& (packageInfo.activities.length > 0)) {
			if (TextUtils.isEmpty(mPluginClientActivityClass)) {
				mPluginClientActivityClass = packageInfo.activities[0].name;
			}
			for (ActivityInfo a : packageInfo.activities) {
				if (a.name.equals(mPluginClientActivityClass)) {
					mActivityInfo = a;
				}
			}
		}
	}

	private void handleActivityInfo() {
		if (mActivityInfo.theme > 0) {
			mPluginHostActivity.setTheme(mActivityInfo.theme);
		}
		Theme superTheme = mPluginHostActivity.getTheme();
		mTheme = mResources.newTheme();
		mTheme.setTo(superTheme);

		// TODO: handle mActivityInfo.launchMode here in the future.
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void launchTargetActivity() {
		try {
			Class<?> localClass = getClassLoader().loadClass(
					mPluginClientActivityClass);
			Constructor<?> localConstructor = localClass
					.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});
			if (!(instance instanceof IPluginActivity)) {
				throw new IllegalArgumentException();
			}
			mDelegatedActivity = (IPluginActivity) instance;
			((IPluginActivityDelegate) mPluginHostActivity)
					.attach(mDelegatedActivity);

			mDelegatedActivity.setDelegate(mPluginHostActivity, mPluginClient);

			Bundle bundle = new Bundle();
			mDelegatedActivity.onCreate(bundle);
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

	public IPluginActivity getRemoteActivity() {
		return mDelegatedActivity;
	}
}
