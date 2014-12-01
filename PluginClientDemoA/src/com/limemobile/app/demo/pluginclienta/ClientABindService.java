package com.limemobile.app.demo.pluginclienta;

import java.io.FileDescriptor;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.Toast;

import com.limemobile.app.plugin.PluginClientService;

public class ClientABindService extends PluginClientService {

	public static class ClientBinder implements IBinder {

		@Override
		public String getInterfaceDescriptor() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean pingBinder() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isBinderAlive() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public IInterface queryLocalInterface(String descriptor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void dump(FileDescriptor fd, String[] args)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void dumpAsync(FileDescriptor fd, String[] args)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean transact(int code, Parcel data, Parcel reply, int flags)
				throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void linkToDeath(DeathRecipient recipient, int flags)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
			// TODO Auto-generated method stub
			return false;
		}

		public void showToast(Context context) {
			Toast.makeText(context, R.string.app_name, Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new ClientBinder();
	}

}
