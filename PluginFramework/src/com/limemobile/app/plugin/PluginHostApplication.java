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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;

import com.limemobile.app.plugin.internal.PluginClientInfo;
import com.limemobile.app.plugin.internal.PluginClientManager;
import com.limemobile.app.plugin.internal.ReflectFieldAccessor;

/**
 * PluginHost中Application需要继承自PluginHostApplication，
 * 并且如果想支持PluginClient运行在不同的Process下，则需要在loadPluginClients()中加载相应的PluginClient
 * 
 * @author achellies
 * 
 */
public class PluginHostApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			/**
			 * 一个Android应用在启动时，首先Dalvik加载的是Android自身的框架。之后会加载APK包中的classes.
			 * dex文件到全局的ClassLoader
			 * 。最后根据AndroidManifest.xml中指定的类名，创建对应的Activity实例来展示UI。
			 * 
			 * Android通过dalvik.system.DexClassLoader提供了动态加载Java代码的能力，
			 * 如果我们能够在Activity启动之前
			 * ，替换全局的ClassLoader（Application.mBase.mPackageInfo.mClassLoader）
			 * 这里主要是解决多个apk共用一个jar包的问题（例如：多个plugin client共用一个通用的jar）
			 * 
			 * http://www.trinea.cn/android/java-loader-common-class/
			 * 
			 * @see TODO 暂时未支持
			 */
			Context baseContext = new ReflectFieldAccessor<Context>(this,
					"mBase").get();

			Object packageInfo = new ReflectFieldAccessor<Object>(baseContext,
					"mPackageInfo").get();

			ReflectFieldAccessor<ClassLoader> sClassLoader = new ReflectFieldAccessor<ClassLoader>(
					packageInfo, "mClassLoader");
			ClassLoader classLoader = sClassLoader.get();
			PluginClientManager.sharedInstance(baseContext)
					.setPluginHostGlobalClassLoader(classLoader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadPluginClients();
	}

	/**
	 * 加载内置的plugin
	 * client,只有在Application启动时加载到PluginManager中的PluginClient才能支持运行在不同的Process
	 * Activity或者Service需要运行在不同的Process可以在AndroidManifest.xml中指定
	 * android:process=":remote"
	 */
	protected void loadPluginClients() {
	}

	// TODO 如果client中Application
	// Context来启动Activity和Service的话，因为没法获取到启动的包名，所以这里的方法有些ugly
	@Override
	public void startActivity(Intent intent) {
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			super.startActivity(intent);
		} else {
			ComponentName componentName = intent.getComponent();
			if (componentName == null) {
				throw new NullPointerException("Not Support Null ComponentName");
			}
			Collection<PluginClientInfo> clients = PluginClientManager
					.sharedInstance(this).getPluginClients();
			if (!clients.isEmpty()) {
				Iterator<PluginClientInfo> iterator = clients.iterator();

				while (iterator.hasNext()) {
					PluginClientInfo clientInfo = iterator.next();

					ActivityInfo[] activities = clientInfo.mClientPackageInfo.activities;
					if (activities != null) {
						for (ActivityInfo activityInfo : activities) {
							if (activityInfo.name.equals(componentName
									.getClassName())) {
								intent.setPackage(clientInfo.mPackageName);
							}
						}
					}
				}
			}
			PluginClientManager.sharedInstance(this)
					.startActivity(this, intent);
		}
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			super.startActivity(intent, options);
		} else {
			ComponentName componentName = intent.getComponent();
			if (componentName == null) {
				throw new NullPointerException("Not Support Null ComponentName");
			}
			Collection<PluginClientInfo> clients = PluginClientManager
					.sharedInstance(this).getPluginClients();
			if (!clients.isEmpty()) {
				Iterator<PluginClientInfo> iterator = clients.iterator();

				while (iterator.hasNext()) {
					PluginClientInfo clientInfo = iterator.next();

					ActivityInfo[] activities = clientInfo.mClientPackageInfo.activities;
					if (activities != null) {
						for (ActivityInfo activityInfo : activities) {
							if (activityInfo.name.equals(componentName
									.getClassName())) {
								intent.setPackage(clientInfo.mPackageName);
							}
						}
					}
				}
			}
			PluginClientManager.sharedInstance(this).startActivity(this,
					intent, options);
		}
	}

	@Override
	public ComponentName startService(Intent service) {
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			return super.startService(service);
		} else {
			ComponentName componentName = service.getComponent();
			if (componentName == null) {
				throw new NullPointerException("Not Support Null ComponentName");
			}
			Collection<PluginClientInfo> clients = PluginClientManager
					.sharedInstance(this).getPluginClients();
			if (!clients.isEmpty()) {
				Iterator<PluginClientInfo> iterator = clients.iterator();

				while (iterator.hasNext()) {
					PluginClientInfo clientInfo = iterator.next();

					ServiceInfo[] services = clientInfo.mClientPackageInfo.services;
					if (services != null) {
						for (ServiceInfo serviceInfo : services) {
							if (serviceInfo.name.equals(componentName
									.getClassName())) {
								service.setPackage(clientInfo.mPackageName);
							}
						}
					}
				}
			}
		}
		return PluginClientManager.sharedInstance(this).startService(this,
				service);
	}

	@Override
	public boolean stopService(Intent service) {
		List<ResolveInfo> resolveInfos = getPackageManager()
				.queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			return super.stopService(service);
		} else {
			ComponentName componentName = service.getComponent();
			if (componentName == null) {
				throw new NullPointerException("Not Support Null ComponentName");
			}
			Collection<PluginClientInfo> clients = PluginClientManager
					.sharedInstance(this).getPluginClients();
			if (!clients.isEmpty()) {
				Iterator<PluginClientInfo> iterator = clients.iterator();

				while (iterator.hasNext()) {
					PluginClientInfo clientInfo = iterator.next();

					ServiceInfo[] services = clientInfo.mClientPackageInfo.services;
					if (services != null) {
						for (ServiceInfo serviceInfo : services) {
							if (serviceInfo.name.equals(componentName
									.getClassName())) {
								service.setPackage(clientInfo.mPackageName);
							}
						}
					}
				}
			}
		}
		return PluginClientManager.sharedInstance(this).stopService(this,
				service);
	}

}
