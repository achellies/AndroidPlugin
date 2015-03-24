package com.limemobile.app.plugin;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class PluginClientFragment extends Fragment {
    protected Activity mAttachtedActivity;

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof PluginHostDelegateFragmentActivity) {
            mAttachtedActivity = (Activity) ((PluginHostDelegateFragmentActivity) activity)
                    .getRemoteActivity();
        } else {
            mAttachtedActivity = activity;
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mAttachtedActivity = null;
        super.onDetach();
    }

}
