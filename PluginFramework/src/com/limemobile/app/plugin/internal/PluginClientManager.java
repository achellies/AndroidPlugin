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
package com.limemobile.app.plugin.internal;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.limemobile.app.plugin.PluginClientActivity;
import com.limemobile.app.plugin.PluginClientApplication;
import com.limemobile.app.plugin.PluginClientDialogActivity;
import com.limemobile.app.plugin.PluginClientDialogFragmentActivity;
import com.limemobile.app.plugin.PluginClientFragmentActivity;
import com.limemobile.app.plugin.PluginClientService;
import com.limemobile.app.plugin.PluginClientTransparentFragmentActivity;
import com.limemobile.app.plugin.PluginHostDelegateActivity;
import com.limemobile.app.plugin.PluginHostDelegateDialogActivity;
import com.limemobile.app.plugin.PluginHostDelegateDialogFragmentActivity;
import com.limemobile.app.plugin.PluginHostDelegateFragmentActivity;
import com.limemobile.app.plugin.PluginHostDelegateService;
import com.limemobile.app.plugin.PluginHostDelegateTransparentActivity;
import com.limemobile.app.plugin.PluginHostDelegateTransparentFragmentActivity;

import dalvik.system.DexClassLoader;

public class PluginClientManager {
	static final String INTENT_EXTRA_PLUGIN_CLIENT_DEX_PATH = "extra_plugin_dex_path";
	static final String INTENT_EXTRA_PLUGIN_CLIENT_ACTIVITY_CLASS = "extra_plugin_activity_class";
	static final String INTENT_EXTRA_PLUGIN_CLIENT_SERVICE_CLASS = "extra_plugin_service_class";
	static final String INTENT_EXTRA_PLUGIN_CLIENT_CONTENT_PROVIDER_CLASS = "extra_plugin_content_provider_class";
	static final String INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME = "extra_plugin_packagename";

    private static volatile PluginClientManager sInstance;

    private Context mContext;
    private final Map<String, PluginClientInfo> mPluginClientPackages = Collections
            .synchronizedMap(new HashMap<String, PluginClientInfo>());
    /**
     * 存储plugin client 的packageName和dex文件的对应关系
     */
    private final Map<String, String> mPluginClientDexPaths = Collections
            .synchronizedMap(new HashMap<String, String>());

    /**
     * 因无法控制classLoader的卸载过程，这里需要保存client对应的classLoader，以便在同一次使用时反复卸载和加载，
     * 导致生成的DexClassLoader不一致的问题
     */
    private final Map<String, DexClassLoader> mPluginClientDexClassLoaders = Collections
            .synchronizedMap(new HashMap<String, DexClassLoader>());

    /**
     * 一个Android应用在启动时，首先Dalvik加载的是Android自身的框架。之后会加载APK包中的classes.
     * dex文件到全局的ClassLoader。最后根据AndroidManifest.xml中指定的类名，创建对应的Activity实例来展示UI。
     * 
     * Android通过dalvik.system.DexClassLoader提供了动态加载Java代码的能力，如果我们能够在Activity启动之前
     * ，替换全局的ClassLoader（Application.mBase.mPackageInfo.mClassLoader）
     */
    private ClassLoader mPluginHostGlobalClassLoader;

	private PluginClientManager(Context context) {
		mContext = context.getApplicationContext();
	}

	public static PluginClientManager sharedInstance(Context context) {
		if (sInstance == null) {
			synchronized (PluginClientManager.class) {
				if (sInstance == null) {
					sInstance = new PluginClientManager(context);
				}
			}
		}
		return sInstance;
	}

	/**
	 * 设置PluginHost全局的DexClassLoader
	 * 
	 * @param classLoader
	 */
	public void setPluginHostGlobalClassLoader(ClassLoader classLoader) {
		this.mPluginHostGlobalClassLoader = classLoader;
	}

	/**
	 * add a apk client. Before start a plugin Activity, we should do this
	 * first.<br/>
	 * NOTE : will only be called by host apk.
	 * 
	 * @param dexPath
	 */
	public PluginClientInfo addPluginClient(String dexPath) {
		// when loadApk is called by host apk, we assume that plugin is invoked
		// by host.
		PackageManager packageManager = mContext.getPackageManager();

		int flags = PackageManager.GET_ACTIVITIES
				| PackageManager.GET_CONFIGURATIONS
				| PackageManager.GET_INSTRUMENTATION
				| PackageManager.GET_PERMISSIONS | PackageManager.GET_PROVIDERS
				| PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES
				| PackageManager.GET_SIGNATURES;

		PackageInfo packageInfo = packageManager.getPackageArchiveInfo(dexPath,
				flags);
		if (packageInfo == null) {
			return null;
		}

        final String packageName = packageInfo.packageName;
        PluginClientInfo pluginPackage = mPluginClientPackages.get(packageName);
        if (pluginPackage == null) {
            DexClassLoader dexClassLoader = null;
            if (mPluginClientDexClassLoaders.containsKey(packageName)) {
                dexClassLoader = mPluginClientDexClassLoaders.get(packageName);
            } else {
                dexClassLoader = createDexClassLoader(dexPath,
                        packageInfo.packageName, packageInfo.versionName);
                mPluginClientDexClassLoaders.put(packageName, dexClassLoader);
            }

            AssetManager assetManager = createAssetManager(dexPath);
            Resources resources = createResources(assetManager);
            pluginPackage = new PluginClientInfo(packageName, dexPath,
                    dexClassLoader, assetManager, resources, packageInfo);
            mPluginClientPackages.put(packageName, pluginPackage);
        }
        if (!mPluginClientDexPaths.containsKey(packageName)) {
            mPluginClientDexPaths.put(packageName, dexPath);
        }
        return pluginPackage;
    }

    public void removePluginClient(String packageName) {
        PluginClientInfo pluginPackage = mPluginClientPackages.get(packageName);
        if (pluginPackage == null) {
            return;
        }
        if (mContext instanceof Application) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                try {
                    Application.class
                            .getMethod(
                                    "unregisterComponentCallbacks",
                                    Class.forName("android.content.ComponentCallbacks"))
                            .invoke(mContext, pluginPackage.getApplication());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (pluginPackage.mApplication.getClass().isAssignableFrom(
                PluginClientApplication.class)) {
            ((PluginClientApplication) pluginPackage.mApplication).onDestroy();
        }
        mPluginClientPackages.remove(packageName);
        mPluginClientDexPaths.remove(packageName);
        pluginPackage = null;
    }

    private DexClassLoader createDexClassLoader(String dexPath,
            String packageName, String version) {
        File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
        dexOutputDir = new File(dexOutputDir, String.format("/%s/%s/",
                packageName, version));
        dexOutputDir.mkdirs();
        final String dexOutputPath = dexOutputDir.getAbsolutePath();

        ApplicationInfo ai = mContext.getApplicationInfo();
        String nativeLibraryDir = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            nativeLibraryDir = ai.nativeLibraryDir;
        } else {
            nativeLibraryDir = "/data/data/" + ai.packageName + "/lib/";
        }
        ClassLoader cl = null;
        Object object = null;
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
             * http://jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1223/2206.html
             * http://www.trinea.cn/android/java-loader-common-class/
             * https://github.com/houkx/android-pluginmgr
             * http://blog.csdn.net/hkxxx/article/details/42194387
             * http://blog.csdn.net/czh0766/article/details/6736826
             * https://github.com/singwhatiwanna/dynamic-load-apk
             * http://blog.csdn.net/jiangwei0910410003/article/details/41384667
             */
            object = ReflectionUtils.getFieldValue(mContext.getApplicationContext(),
                    "mBase.mPackageInfo", true);
            ReflectFieldAccessor<ClassLoader> fieldAccessor = new ReflectFieldAccessor<ClassLoader>(
                    object, "mClassLoader");
            cl = fieldAccessor.get();
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        } catch (NoSuchFieldException e) {
        } finally {
            if (cl == null) {
                cl = mContext.getClassLoader();
            }
        }
        PluginClientDexClassLoader dcl = new PluginClientDexClassLoader(
                dexPath, dexOutputPath, nativeLibraryDir, cl);

//        try {
//            Field f = ClassLoader.class.getDeclaredField("parent");
//            f.setAccessible(true);
//            f.set(cl, dcl);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return dcl;
    }

    private AssetManager createAssetManager(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod(
                    "addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkPluginClientApplication(PluginClientInfo pluginPackage) {
        if (pluginPackage.isApplicationInited()) {
            return;
        }
        String className = pluginPackage.mClientPackageInfo.applicationInfo.className;

        try {
            // create Application instance for plugin
            Application application = null;
            if (className == null) {
                application = new Application();
            } else {
                ClassLoader loader = pluginPackage.mClassLoader;
                Class<?> applicationClass;
                applicationClass = loader.loadClass(className);
                application = (Application) applicationClass.newInstance();
            }
            pluginPackage.setApplication(application);
            //
            PluginContextWrapper ctxWrapper = new PluginContextWrapper(
                    mContext, pluginPackage);
            // set field: mBase
            ReflectionUtils.setFieldValue(application, "mBase", ctxWrapper);
            // set field: mLoadedApk, get from context(framework application)
            Object mLoadedApk = ReflectionUtils.getFieldValue(mContext,
                    "mLoadedApk");
            ReflectionUtils
                    .setFieldValue(application, "mLoadedApk", mLoadedApk);
            if (mContext instanceof Application) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Application.class
                            .getMethod(
                                    "registerComponentCallbacks",
                                    Class.forName("android.content.ComponentCallbacks"))
                            .invoke(mContext, application);
                }
            }
            // invoke plugin application's onCreate()
            application.onCreate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

	public PluginClientInfo getPluginClient(String packageName) {
		PluginClientInfo pluginClient = mPluginClientPackages.get(packageName);
		if (pluginClient == null
				&& mPluginClientDexPaths.containsKey(packageName)) {
			return addPluginClient(mPluginClientDexPaths.get(packageName));
		}
		return pluginClient;
	}

	public Collection<PluginClientInfo> getPluginClients() {
		return mPluginClientPackages.values();
	}

	private Resources createResources(AssetManager assetManager) {
		Resources superRes = mContext.getResources();
		Resources resources = new Resources(assetManager,
				superRes.getDisplayMetrics(), superRes.getConfiguration());
		return resources;
	}

	public ComponentName startService(Context context, Intent service) {
		String packageName = service.getPackage();
		if (TextUtils.isEmpty(packageName)) {
			throw new NullPointerException("disallow null packageName.");
		}

		if (service.getComponent() == null) {
			throw new NullPointerException("disallow null component");
		}

		String className = service.getComponent().getClassName();

		return startPluginClientService(context, service, packageName,
				className);
	}

	public boolean stopService(Context context, Intent service) {
		String packageName = service.getPackage();
		if (TextUtils.isEmpty(packageName)) {
			throw new NullPointerException("disallow null packageName.");
		}

		if (service.getComponent() == null) {
			throw new NullPointerException("disallow null component");
		}

		String className = service.getComponent().getClassName();

		return stopPluginClientService(context, service, packageName, className);
	}

	public boolean bindService(Context context, Intent service,
			ServiceConnection conn, int flags) {
		String packageName = service.getPackage();
		if (TextUtils.isEmpty(packageName)) {
			throw new NullPointerException("disallow null packageName.");
		}

		if (service.getComponent() == null) {
			throw new NullPointerException("disallow null component");
		}

		String className = service.getComponent().getClassName();
		return bindPluginClientService(context, service, packageName,
				className, conn, flags);
	}

	public ComponentName startPluginClientService(Context context,
			String packageName, String className) {
		return startPluginClientService(context, new Intent(), packageName,
				className);
	}

    public ComponentName startPluginClientService(Context context,
            Intent service, String packageName, String className) {
        PluginClientInfo pluginPackage = mPluginClientPackages.get(packageName);
        if (pluginPackage == null) {
            return null;
        }
        checkPluginClientApplication(pluginPackage);
        DexClassLoader classLoader = pluginPackage.mClassLoader;
        className = (className == null ? pluginPackage.getDefaultActivity()
                : className);
        if (className.startsWith(".")) {
            className = packageName + className;
        }
        Class<?> clazz = null;
        if (mPluginHostGlobalClassLoader != null) {
            try {
                clazz = mPluginHostGlobalClassLoader.loadClass(className);
            } catch (Exception e) {
            }
        }

		if (clazz == null) {
			try {
				clazz = classLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}

		Class<? extends Service> serviceClass = null;

		if (PluginClientService.class.isAssignableFrom(clazz)) {
			serviceClass = PluginHostDelegateService.class;
		} else {
			return null;
		}

		service.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_SERVICE_CLASS, className);
		service.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME, packageName);
		service.setClass(context, serviceClass);
		return context.startService(service);
	}

	public boolean bindPluginClientService(Context context, String packageName,
			String className, ServiceConnection conn, int flags) {
		return bindPluginClientService(context, new Intent(), packageName,
				className, conn, flags);
	}

    public boolean bindPluginClientService(Context context, Intent service,
            String packageName, String className, ServiceConnection conn,
            int flags) {
        PluginClientInfo pluginPackage = mPluginClientPackages.get(packageName);
        if (pluginPackage == null) {
            return false;
        }
        checkPluginClientApplication(pluginPackage);
        DexClassLoader classLoader = pluginPackage.mClassLoader;
        className = (className == null ? pluginPackage.getDefaultActivity()
                : className);
        if (className.startsWith(".")) {
            className = packageName + className;
        }
        Class<?> clazz = null;
        if (mPluginHostGlobalClassLoader != null) {
            try {
                clazz = mPluginHostGlobalClassLoader.loadClass(className);
            } catch (Exception e) {
            }
        }

		if (clazz == null) {
			try {
				clazz = classLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}

		Class<? extends Service> serviceClass = null;

		if (PluginClientService.class.isAssignableFrom(clazz)) {
			serviceClass = PluginHostDelegateService.class;
		} else {
			return false;
		}

		service.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_SERVICE_CLASS, className);
		service.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME, packageName);
		service.setClass(context, serviceClass);

		return context.bindService(service, conn, flags);
	}

	public boolean stopPluginClientService(Context context, String packageName,
			String className) {
		return stopPluginClientService(context, new Intent(), packageName,
				className);
	}

    public boolean stopPluginClientService(Context context, Intent service,
            String packageName, String className) {
        PluginClientInfo pluginPackage = mPluginClientPackages.get(packageName);
        if (pluginPackage == null) {
            return false;
        }
        checkPluginClientApplication(pluginPackage);
        DexClassLoader classLoader = pluginPackage.mClassLoader;
        className = (className == null ? pluginPackage.getDefaultActivity()
                : className);
        if (className.startsWith(".")) {
            className = packageName + className;
        }
        Class<?> clazz = null;
        if (mPluginHostGlobalClassLoader != null) {
            try {
                clazz = mPluginHostGlobalClassLoader.loadClass(className);
            } catch (Exception e) {
            }
        }

		if (clazz == null) {
			try {
				clazz = classLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}

		Class<? extends Service> serviceClass = null;

		if (PluginClientService.class.isAssignableFrom(clazz)) {
			serviceClass = PluginHostDelegateService.class;
		} else {
			return false;
		}

		service.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_SERVICE_CLASS, className);
		service.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME, packageName);
		service.setClass(context, serviceClass);
		return context.stopService(service);
	}

	public void startPluginClientActivity(Context context, String packageName,
			String className) {
		startPluginActivityForResult(context, new Intent(), packageName,
				className, -1, null);
	}

	public void startActivityForResult(Context context, Intent intent,
			int requestCode) {
		startPluginActivityForResult(context, intent, requestCode, null);
	}

	public void startActivityForResult(Context context, Intent intent,
			int requestCode, Bundle options) {
		startPluginActivityForResult(context, intent, requestCode, options);
	}

	public void startActivity(Context context, Intent intent) {
		startPluginActivityForResult(context, intent, -1, null);
	}

	public void startActivity(Context context, Intent intent, Bundle options) {
		startPluginActivityForResult(context, intent, -1, options);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void startPluginActivityForResult(Context context, Intent intent,
			int requestCode, Bundle options) {
		String packageName = intent.getPackage();
		if (TextUtils.isEmpty(packageName)) {
			throw new NullPointerException("disallow null packageName.");
		}

		if (intent.getComponent() == null) {
			throw new NullPointerException("disallow null component");
		}

		String className = intent.getComponent().getClassName();

		startPluginActivityForResult(context, intent, packageName, className,
				requestCode, options);
	}

    private void startPluginActivityForResult(Context context, Intent intent,
            String packageName, String className, int requestCode,
            Bundle options) {
        // TODO 是否不需要传入Intent
        PluginClientInfo pluginPackage = mPluginClientPackages.get(packageName);
        if (pluginPackage == null) {
            return;
        }
        checkPluginClientApplication(pluginPackage);
        DexClassLoader classLoader = pluginPackage.mClassLoader;
        className = (className == null ? pluginPackage.getDefaultActivity()
                : className);
        if (className.startsWith(".")) {
            className = packageName + className;
        }
        Class<?> clazz = null;
        if (mPluginHostGlobalClassLoader != null) {
            try {
                clazz = mPluginHostGlobalClassLoader.loadClass(className);
            } catch (Exception e) {
            }
        }

		if (clazz == null) {
			try {
				clazz = classLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}
		}

		Class<? extends Activity> activityClass = null;

        if (PluginClientDialogActivity.class.isAssignableFrom(clazz)) {
            activityClass = PluginHostDelegateDialogActivity.class;
        } else if (PluginClientDialogFragmentActivity.class
                .isAssignableFrom(clazz)) {
            activityClass = PluginHostDelegateTransparentActivity.class;
        } else if (PluginClientActivity.class.isAssignableFrom(clazz)) {
            activityClass = PluginHostDelegateActivity.class;
        } else if (PluginClientDialogFragmentActivity.class
                .isAssignableFrom(clazz)) {
            activityClass = PluginHostDelegateDialogFragmentActivity.class;
        } else if (PluginClientTransparentFragmentActivity.class
                .isAssignableFrom(clazz)) {
            activityClass = PluginHostDelegateTransparentFragmentActivity.class;
        } else if (PluginClientFragmentActivity.class.isAssignableFrom(clazz)) {
            activityClass = PluginHostDelegateFragmentActivity.class;
        } else {
            return;
        }

		// TODO 处理options
		intent.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_ACTIVITY_CLASS, className);
		intent.putExtra(INTENT_EXTRA_PLUGIN_CLIENT_PACKAGE_NAME, packageName);
		intent.setClass(context, activityClass);
		performStartActivityForResult(context, intent, requestCode);
	}

	private void performStartActivityForResult(Context context, Intent intent,
			int requestCode) {
		if (context instanceof Activity) {
			((Activity) context).startActivityForResult(intent, requestCode);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = null;
		try {
			pkgInfo = pm.getPackageArchiveInfo(apkFilepath,
					PackageManager.GET_ACTIVITIES);
		} catch (Exception e) {
			// should be something wrong with parse
			e.printStackTrace();
		}

		return pkgInfo;
	}

	public static Drawable getAppIcon(Context context, String apkFilepath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return null;
		}

		// Workaround for http://code.google.com/p/android/issues/detail?id=9151
		ApplicationInfo appInfo = pkgInfo.applicationInfo;
		if (Build.VERSION.SDK_INT >= 8) {
			appInfo.sourceDir = apkFilepath;
			appInfo.publicSourceDir = apkFilepath;
		}

		return pm.getApplicationIcon(appInfo);
	}

	public static String getAppLabel(Context context, String apkFilepath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return null;
		}

		// Workaround for http://code.google.com/p/android/issues/detail?id=9151
		ApplicationInfo appInfo = pkgInfo.applicationInfo;
		if (Build.VERSION.SDK_INT >= 8) {
			appInfo.sourceDir = apkFilepath;
			appInfo.publicSourceDir = apkFilepath;
		}

		return pm.getApplicationLabel(appInfo).toString();
	}

	public static int getAppVersionCode(Context context, String apkFilepath) {
		int version = 0;
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return version;
		}
		version = pkgInfo.versionCode;
		return version;
	}

	public static String getAppVersionName(Context context, String apkFilepath) {
		String version = "";
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return version;
		}
		version = pkgInfo.versionName;
		return version;
	}
}
