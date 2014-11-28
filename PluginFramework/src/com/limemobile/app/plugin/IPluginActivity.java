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

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

import com.limemobile.app.plugin.internal.PluginClientInfo;

public interface IPluginActivity {

    public void setDelegate(Activity pluginHostActivity,
            PluginClientInfo pluginPackage);

    public void onCreate(Bundle savedInstanceState);

    public void onStart();

    public void onRestart();

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onResume();

    public void onPause();

    public void onStop();

    public void onDestroy();

    public void onSaveInstanceState(Bundle outState);

    public void onNewIntent(Intent intent);

    public void onRestoreInstanceState(Bundle savedInstanceState);

    public boolean onTouchEvent(MotionEvent event);

    public boolean onKeyUp(int keyCode, KeyEvent event);

    public void onWindowAttributesChanged(LayoutParams params);

    public void onWindowFocusChanged(boolean hasFocus);

    public void onBackPressed();

    public boolean onCreateOptionsMenu(Menu menu);

    public boolean onOptionsItemSelected(MenuItem item);
    
    public void unbindService(ServiceConnection conn);

    public String getPackageName();
}
