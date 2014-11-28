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

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.limemobile.app.plugin.internal.PluginClientInfo;
import com.limemobile.app.plugin.internal.PluginClientManager;

public class PluginClientActivity extends Activity implements IPluginActivity {
	protected Activity mContext;

	protected Activity mProxyActivity;

	protected PluginClientInfo mPluginPackage;

	@Override
	public void setDelegate(Activity proxyActivity,
			PluginClientInfo pluginPackage) {
		mProxyActivity = (Activity) proxyActivity;
		mPluginPackage = pluginPackage;
		mContext = mProxyActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (mProxyActivity == null) {
			mContext = this;
			super.onCreate(savedInstanceState);
		}
	}

	@Override
	public void setContentView(View view) {
		if (mProxyActivity == null) {
			super.setContentView(view);
		} else {
			mProxyActivity.setContentView(view);
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if (mProxyActivity == null) {
			super.setContentView(view, params);
		} else {
			mProxyActivity.setContentView(view, params);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		if (mProxyActivity == null) {
			super.setContentView(layoutResID);
		} else {
			mProxyActivity.setContentView(layoutResID);
		}
	}

	@Override
	public void addContentView(View view, LayoutParams params) {
		if (mProxyActivity == null) {
			super.addContentView(view, params);
		} else {
			mProxyActivity.addContentView(view, params);
		}
	}

	@Override
	public View findViewById(int id) {
		if (mProxyActivity == null) {
			return super.findViewById(id);
		} else {
			return mProxyActivity.findViewById(id);
		}
	}

	@Override
	public Intent getIntent() {
		if (mProxyActivity == null) {
			return super.getIntent();
		} else {
			return mProxyActivity.getIntent();
		}
	}

	@Override
	public ClassLoader getClassLoader() {
		if (mProxyActivity == null) {
			return super.getClassLoader();
		} else {
			return mProxyActivity.getClassLoader();
		}
	}

	@Override
	public Resources getResources() {
		if (mProxyActivity == null) {
			return super.getResources();
		} else {
			return mProxyActivity.getResources();
		}
	}

	@Override
	public Resources.Theme getTheme() {
		if (mProxyActivity == null) {
			return super.getTheme();
		} else {
			return mProxyActivity.getTheme();
		}
	}

	@Override
	public String getPackageName() {
		if (mProxyActivity == null) {
			return super.getPackageName();
		} else {
			return mPluginPackage.mPackageName;
		}
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		if (mProxyActivity == null) {
			return super.getLayoutInflater();
		} else {
			return mProxyActivity.getLayoutInflater();
		}
	}

	@Override
	public MenuInflater getMenuInflater() {
		if (mProxyActivity == null) {
			return super.getMenuInflater();
		} else {
			return mProxyActivity.getMenuInflater();
		}
	}

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		if (mProxyActivity == null) {
			return super.getSharedPreferences(name, mode);
		} else {
			return mProxyActivity.getSharedPreferences(name, mode);
		}
	}

	@Override
	public Context getApplicationContext() {
		if (mProxyActivity == null) {
			return super.getApplicationContext();
		} else {
			return mProxyActivity.getApplicationContext();
		}
	}

	@Override
	public WindowManager getWindowManager() {
		if (mProxyActivity == null) {
			return super.getWindowManager();
		} else {
			return mProxyActivity.getWindowManager();
		}
	}

	@Override
	public Window getWindow() {
		if (mProxyActivity == null) {
			return super.getWindow();
		} else {
			return mProxyActivity.getWindow();
		}
	}

	@Override
	public Object getSystemService(String name) {
		if (mProxyActivity == null) {
			return super.getSystemService(name);
		} else {
			return mProxyActivity.getSystemService(name);
		}
	}

	@Override
	public void finish() {
		if (mProxyActivity == null) {
			super.finish();
		} else {
			mProxyActivity.finish();
		}
	}

	@Override
	public void onBackPressed() {
		if (mProxyActivity == null) {
			super.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mProxyActivity == null) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onStart() {
		if (mProxyActivity == null) {
			super.onStart();
		}
	}

	@Override
	public void onRestart() {
		if (mProxyActivity == null) {
			super.onRestart();
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (mProxyActivity == null) {
			super.onRestoreInstanceState(savedInstanceState);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mProxyActivity == null) {
			super.onSaveInstanceState(outState);
		}
	}

	public void onNewIntent(Intent intent) {
		if (mProxyActivity == null) {
			super.onNewIntent(intent);
		}
	}

	@Override
	public void onResume() {
		if (mProxyActivity == null) {
			super.onResume();
		}
	}

	@Override
	public void onPause() {
		if (mProxyActivity == null) {
			super.onPause();
		}
	}

	@Override
	public void onStop() {
		if (mProxyActivity == null) {
			super.onStop();
		}
	}

	@Override
	public void onDestroy() {
		if (mProxyActivity == null) {
			super.onDestroy();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (mProxyActivity == null) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mProxyActivity == null) {
			return super.onKeyUp(keyCode, event);
		}
		return false;
	}

	public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
		if (mProxyActivity == null) {
			super.onWindowAttributesChanged(params);
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		if (mProxyActivity == null) {
			super.onWindowFocusChanged(hasFocus);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		if (mProxyActivity == null) {
			return super.onCreateOptionsMenu(menu);
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (mProxyActivity == null) {
			return onOptionsItemSelected(item);
		}
		return false;
	}

	@Override
	public Context getBaseContext() {
		if (mProxyActivity == null) {
			return super.getBaseContext();
		}

		return mProxyActivity.getBaseContext();
	}

	@Override
	public AssetManager getAssets() {
		if (mProxyActivity == null) {
			return super.getAssets();
		}
		return mProxyActivity.getAssets();
	}

	@Override
	public ContentResolver getContentResolver() {
		if (mProxyActivity == null) {
			return super.getContentResolver();
		}
		return mProxyActivity.getContentResolver();
	}

	@Override
	public ApplicationInfo getApplicationInfo() {
		if (mProxyActivity == null) {
			return super.getApplicationInfo();
		}
		return mProxyActivity.getApplicationInfo();
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		if (mProxyActivity == null) {
			super.unbindService(conn);
			return;
		}
		mProxyActivity.unbindService(conn);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (mProxyActivity == null) {
			super.startActivityForResult(intent, requestCode);
			return;
		}

		List<ResolveInfo> resolveInfos = mContext.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			super.startActivityForResult(intent, requestCode);
		} else {
			intent.setPackage(mPluginPackage.mPackageName);
			PluginClientManager.sharedInstance(mContext)
					.startActivityForResult(mContext, intent, requestCode);
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		if (mProxyActivity == null) {
			super.startActivityForResult(intent, requestCode, options);
			return;
		}

		List<ResolveInfo> resolveInfos = mContext.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			super.startActivityForResult(intent, requestCode, options);
		} else {
			intent.setPackage(mPluginPackage.mPackageName);
			PluginClientManager.sharedInstance(mContext)
					.startActivityForResult(mContext, intent, requestCode,
							options);
		}
	}

	@Override
	public void startActivity(Intent intent) {
		if (mProxyActivity == null) {
			super.startActivity(intent);
		}

		List<ResolveInfo> resolveInfos = mContext.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			super.startActivity(intent);
		} else {
			intent.setPackage(mPluginPackage.mPackageName);
			PluginClientManager.sharedInstance(mContext).startActivity(
					mContext, intent);
		}
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		if (mProxyActivity == null) {
			super.startActivity(intent, options);
		}

		List<ResolveInfo> resolveInfos = mContext.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			super.startActivity(intent, options);
		} else {
			intent.setPackage(mPluginPackage.mPackageName);
			PluginClientManager.sharedInstance(mContext).startActivity(
					mContext, intent, options);
		}
	}

	@Override
	public ComponentName startService(Intent service) {
		if (mProxyActivity == null) {
			return super.startService(service);
		}
		List<ResolveInfo> resolveInfos = mContext
				.getPackageManager()
				.queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			return super.startService(service);
		} else {
			service.setPackage(mPluginPackage.mPackageName);
			return PluginClientManager.sharedInstance(mContext).startService(
					mContext, service);
		}
	}

	@Override
	public boolean stopService(Intent service) {
		if (mProxyActivity == null) {
			return super.stopService(service);
		}
		List<ResolveInfo> resolveInfos = mContext
				.getPackageManager()
				.queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			return super.stopService(service);
		} else {
			service.setPackage(mPluginPackage.mPackageName);
			return PluginClientManager.sharedInstance(mContext).stopService(
					mContext, service);
		}
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		if (mProxyActivity == null) {
			return super.bindService(service, conn, flags);
		} else {
			return mProxyActivity.bindService(service, conn, flags);
		}
	}

}
