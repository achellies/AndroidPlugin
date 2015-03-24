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

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;

class PluginContextWrapper extends ContextWrapper {
    private PluginClientInfo mPluginClient;

    public PluginContextWrapper(Context base, PluginClientInfo client) {
        super(base);
        mPluginClient = client;
    }

    @Override
    public File getFilesDir() {
        return super.getFilesDir();
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public String getPackageName() {
        return super.getPackageName();
    }

    @Override
    public Resources getResources() {
        return mPluginClient.mResources;
    }

    @Override
    public AssetManager getAssets() {
        return mPluginClient.mAssetManager;
    }
}
