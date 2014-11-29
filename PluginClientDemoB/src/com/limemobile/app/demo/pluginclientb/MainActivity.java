package com.limemobile.app.demo.pluginclientb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limemobile.app.demo.pluginclientb.R;
import com.limemobile.app.demo.pluginhost.TestHostClass;
import com.limemobile.app.plugin.PluginClientActivity;
import com.limemobile.app.plugin.internal.PluginClientManager;

public class MainActivity extends PluginClientActivity {

	private static final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView(savedInstanceState);
	}

	private void initView(Bundle savedInstanceState) {
		// mContext.setContentView(generateContentView(mContext));
		setContentView(R.layout.activity_main);

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TestHostClass testHostClass = new TestHostClass();
				testHostClass.testMethod(mContext);
			}

		});

		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PluginClientManager.sharedInstance(mContext)
						.startPluginClientService(mContext,
								"com.limemobile.app.demo.pluginclienta",
								".ClientAStartedService");
			}

		});
	}

	private View generateContentView(final Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		Button button = new Button(context);
		button.setText("Invoke host method");
		layout.addView(button, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TestHostClass testHostClass = new TestHostClass();
				testHostClass.testMethod(mContext);
			}
		});

		TextView textView = new TextView(context);
		textView.setText("Hello, I'm Plugin B.");
		textView.setTextSize(30);
		layout.addView(textView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		return layout;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult resultCode=" + resultCode);
		if (resultCode == RESULT_FIRST_USER) {
			mContext.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
