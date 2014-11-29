package com.limemobile.app.demo.pluginhost;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class HostStartedService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		if ("start".equals(action)) {
			Toast.makeText(this, "host start", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "host stop", Toast.LENGTH_SHORT).show();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
