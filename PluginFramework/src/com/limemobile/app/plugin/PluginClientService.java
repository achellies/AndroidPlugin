package com.limemobile.app.plugin;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.limemobile.app.plugin.internal.PluginClientInfo;

public class PluginClientService extends Service implements IPluginService {
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
        } else {
            mProxyService.onStart(intent, startId);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mProxyService == null) {
            return super.onStartCommand(intent, flags, startId);
        } else {
            return mProxyService.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy() {
        if (mProxyService == null) {
            super.onDestroy();
        } else {
            mProxyService.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mProxyService == null) {
            super.onConfigurationChanged(newConfig);
        } else {
            mProxyService.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        if (mProxyService == null) {
            super.onLowMemory();
        } else {
            mProxyService.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (mProxyService == null) {
            super.onTrimMemory(level);
        } else {
            mProxyService.onTrimMemory(level);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mProxyService == null) {
            return null;
        } else {
            return mProxyService.onBind(intent);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mProxyService == null) {
            return super.onUnbind(intent);
        } else {
            return mProxyService.onUnbind(intent);
        }
    }

    @Override
    public void onRebind(Intent intent) {
        if (mProxyService == null) {
            super.onRebind(intent);
        } else {
            mProxyService.onRebind(intent);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mProxyService == null) {
            super.onTaskRemoved(rootIntent);
        } else {
            mProxyService.onTaskRemoved(rootIntent);
        }
    }

}
