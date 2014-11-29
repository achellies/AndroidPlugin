package com.limemobile.app.demo.pluginclienta;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.limemobile.app.plugin.PluginClientService;

public class ClientAStartedService extends PluginClientService {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		if ("start".equals(action)) {
			Toast.makeText(mContext, "client A start", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, "client A stop", Toast.LENGTH_SHORT).show();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
