package com.limemobile.app.demo.pluginclientb;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.limemobile.app.plugin.PluginClientService;

public class ClientBStartedService extends PluginClientService {

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
			Toast.makeText(mContext, "client B start", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(mContext, "client B stop " + action,
					Toast.LENGTH_SHORT).show();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(mContext, "client B onDestroy ",
				Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
}
