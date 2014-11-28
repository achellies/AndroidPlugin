package com.limemobile.app.plugin;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;

import com.limemobile.app.plugin.internal.IPluginContentProviderDelegate;
import com.limemobile.app.plugin.internal.PluginDelegateContentProviderImpl;

public class PluginHostDelegateContentProvider extends ContentProvider
		implements IPluginContentProviderDelegate {

	protected IPluginContentProvider mDelegatedContentProvider;
	protected PluginDelegateContentProviderImpl mDelegateImpl = new PluginDelegateContentProviderImpl(
			this);

	@Override
	public boolean onCreate() {
		mDelegatedContentProvider.onCreate();
		return true;
	}

	@Override
	public void attach(IPluginContentProvider delegatedContentProvider) {
		mDelegatedContentProvider = delegatedContentProvider;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mDelegatedContentProvider.onConfigurationChanged(newConfig);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		mDelegatedContentProvider.onLowMemory();
		super.onLowMemory();
	}

	@Override
	public void attachInfo(Context context, ProviderInfo info) {
		mDelegatedContentProvider.attachInfo(context, info);
		super.attachInfo(context, info);
	}

	@Override
	public void shutdown() {
		mDelegatedContentProvider.shutdown();
		super.shutdown();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return mDelegatedContentProvider.query(uri, projection, selection,
				selectionArgs, sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		return mDelegatedContentProvider.getType(uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return mDelegatedContentProvider.insert(uri, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return mDelegatedContentProvider.delete(uri, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return mDelegatedContentProvider.update(uri, values, selection,
				selectionArgs);
	}

}
