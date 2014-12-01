package com.limemobile.app.plugin.internal;

import java.lang.reflect.Constructor;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.text.TextUtils;

import com.limemobile.app.plugin.IPluginContentProvider;

public class PluginDelegateContentProviderImpl {
	protected String mPluginClientContentProviderClass;
	protected String mPluginClientPackageName;

	protected PluginClientInfo mPluginClient;
	protected PluginClientManager mPluginManager;

	protected AssetManager mAssetManager;
	protected Resources mResources;
	protected Theme mTheme;

	protected ProviderInfo mProviderInfo;
	protected ContentProvider mPluginHostContentProvider;
	protected IPluginContentProvider mDelegatedContentProvider;

	public PluginDelegateContentProviderImpl(ContentProvider provider) {
		mPluginHostContentProvider = provider;
	}

	public void onCreate(Intent intent) {
		mPluginClientPackageName = intent
				.getStringExtra(PluginClientManager.INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME);
		mPluginClientContentProviderClass = intent
				.getStringExtra(PluginClientManager.INTENT_EXTRA_PLUGIN_CLIENT_CONTENT_PROVIDER_CLASS);

		mPluginManager = PluginClientManager
				.sharedInstance(mPluginHostContentProvider.getContext());
		mPluginClient = mPluginManager
				.getPluginClient(mPluginClientPackageName);
		mAssetManager = mPluginClient.mAssetManager;
		mResources = mPluginClient.mResources;

		initializeContentProviderInfo();

		launchTargetContentProvider();
	}

	private void initializeContentProviderInfo() {
		PackageInfo packageInfo = mPluginClient.mClientPackageInfo;
		if ((packageInfo.providers != null)
				&& (packageInfo.providers.length > 0)) {
			if (TextUtils.isEmpty(mPluginClientContentProviderClass)) {
				mPluginClientContentProviderClass = packageInfo.providers[0].name;
			}
			for (ProviderInfo a : packageInfo.providers) {
				if (a.name.equals(mPluginClientContentProviderClass)) {
					mProviderInfo = a;
				}
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void launchTargetContentProvider() {
		try {
			Class<?> localClass = getClassLoader().loadClass(
					mPluginClientContentProviderClass);
			Constructor<?> localConstructor = localClass
					.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});
			if (!(instance instanceof IPluginContentProvider)) {
				throw new IllegalArgumentException();
			}
			mDelegatedContentProvider = (IPluginContentProvider) instance;
			((IPluginContentProviderDelegate) mPluginHostContentProvider)
					.attach(mDelegatedContentProvider);

			mDelegatedContentProvider.setDelegate(mPluginHostContentProvider,
					mPluginClient);

			mDelegatedContentProvider.onCreate();
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

	public IPluginContentProvider getRemoteContentProvider() {
		return mDelegatedContentProvider;
	}

}