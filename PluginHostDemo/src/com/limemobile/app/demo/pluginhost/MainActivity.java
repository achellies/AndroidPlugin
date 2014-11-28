package com.limemobile.app.demo.pluginhost;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.limemobile.app.plugin.internal.PluginClientInfo;
import com.limemobile.app.plugin.internal.PluginClientManager;

public class MainActivity extends Activity implements OnItemClickListener {
    private ArrayList<PluginItem> mPluginItems = new ArrayList<PluginItem>();
    private PluginAdapter mPluginAdapter;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mPluginAdapter = new PluginAdapter();
        mListView = (ListView) findViewById(R.id.plugin_list);
    }

    private void initData() {
        Collection<PluginClientInfo> plugins = PluginClientManager
                .sharedInstance(getApplicationContext()).getPluginClients();
        if (plugins == null || plugins.isEmpty()) {
            return;
        }

        for (PluginClientInfo plugin : plugins) {
            PluginItem item = new PluginItem();
            item.pluginPath = plugin.mPath;
            item.packageInfo = PluginClientManager.getPackageInfo(this,
                    item.pluginPath);
            if (item.packageInfo.activities != null
                    && item.packageInfo.activities.length > 0) {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }
            item.versionName = PluginClientManager.getAppVersionName(this,
                    item.pluginPath);
            item.pluginName = PluginClientManager.getAppLabel(this, item.pluginPath);
            mPluginItems.add(item);
        }

        mListView.setAdapter(mPluginAdapter);
        mListView.setOnItemClickListener(this);
        mPluginAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PluginAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public PluginAdapter() {
            mInflater = MainActivity.this.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mPluginItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mPluginItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.plugin_item, parent,
                        false);
                holder = new ViewHolder();
                holder.appIcon = (ImageView) convertView
                        .findViewById(R.id.app_icon);
                holder.appName = (TextView) convertView
                        .findViewById(R.id.app_name);
                holder.versionName = (TextView) convertView
                        .findViewById(R.id.version_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PluginItem item = mPluginItems.get(position);
            PackageInfo packageInfo = item.packageInfo;
            holder.appIcon.setImageDrawable(PluginClientManager.getAppIcon(
                    MainActivity.this, item.pluginPath));
            holder.appName.setText(item.pluginName);
            holder.versionName.setText(item.versionName);
            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView versionName;
    }

    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String pluginName;
        public String launcherActivityName;
        public String versionName;

        public PluginItem() {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        PluginItem item = mPluginItems.get(position);
        PluginClientManager pluginManager = PluginClientManager
                .sharedInstance(this);
        pluginManager.startPluginClientActivity(this, item.packageInfo.packageName,
                item.launcherActivityName);
    }

}
