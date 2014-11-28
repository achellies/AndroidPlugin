package com.limemobile.app.demo.pluginhost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import com.limemobile.app.demo.pluginhost.MainActivity.PluginItem;
import com.limemobile.app.plugin.PluginHostApplication;
import com.limemobile.app.plugin.internal.PluginClientManager;

public class MyApplication extends PluginHostApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void loadPluginClients() {
        AssetManager asset = getAssets();

        try {
            for (String s : asset.list("apks")) {
                File dex = getDir("dex", Context.MODE_PRIVATE);
                dex.mkdir();
                File plugin = new File(dex, s);
                InputStream fis = getAssets().open("apks/" + s);
                FileOutputStream fos = new FileOutputStream(plugin);
                byte[] buffer = new byte[0xFF];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fis.close();
                fos.close();

                PluginClientManager.sharedInstance(this).addPluginClient(
                        plugin.getAbsolutePath());
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

    }
}
