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

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import dalvik.system.DexClassLoader;

/**
 * PluginClient APK. Activities in a same APK share a same AssetManager,
 * Resources and DexClassLoader.
 */
public class PluginClientInfo {
	private String mDefaultActivity;

	public final String mPackageName;
	public final String mPath;

	public final DexClassLoader mClassLoader;
	public final AssetManager mAssetManager;
	public final Resources mResources;
	public final PackageInfo mClientPackageInfo;

	public PluginClientInfo(String packageName, String path,
			DexClassLoader loader, AssetManager assetManager,
			Resources resources, PackageInfo packageInfo) {
		this.mPackageName = packageName;
		this.mPath = path;
		this.mClassLoader = loader;
		this.mAssetManager = assetManager;
		this.mResources = resources;
		this.mClientPackageInfo = packageInfo;
	}

	public String getDefaultActivity() {
		if (TextUtils.isEmpty(mDefaultActivity)) {
			if (mClientPackageInfo.activities != null
					&& mClientPackageInfo.activities.length > 0) {
				mDefaultActivity = mClientPackageInfo.activities[0].name;
			}
		}
		return mDefaultActivity;
	}
}
