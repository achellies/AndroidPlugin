package com.limemobile.app.plugin;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;

import com.limemobile.app.plugin.internal.PluginClientInfo;

public interface IPluginService {

    public void setDelegate(Service pluginHostService,
            PluginClientInfo pluginPackage);

    public void onCreate();

    public void onStart(Intent intent, int startId);

    public int onStartCommand(Intent intent, int flags, int startId);

    public void onDestroy();

    public void onConfigurationChanged(Configuration newConfig);

    public void onLowMemory();

    public void onTrimMemory(int level);

    public IBinder onBind(Intent intent);

    public boolean onUnbind(Intent intent);

    public void onRebind(Intent intent);

    public void unbindService(ServiceConnection conn);

    public void onTaskRemoved(Intent rootIntent);

    public String getPackageName();
}
