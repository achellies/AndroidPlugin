package com.limemobile.app.plugin;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;

import com.limemobile.app.plugin.internal.PluginClientInfo;

public interface IPluginContentProvider {

	public boolean onCreate();

	public void setDelegate(ContentProvider pluginHostContentProvider,
			PluginClientInfo pluginPackage);

	public void onConfigurationChanged(Configuration newConfig);

	public void onLowMemory();

	public void attachInfo(Context context, ProviderInfo info);

	public void shutdown();

	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder);

	public String getType(Uri uri);

	public Uri insert(Uri uri, ContentValues values);

	public int delete(Uri uri, String selection, String[] selectionArgs);

	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs);
}
