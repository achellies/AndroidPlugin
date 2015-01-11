package com.limemobile.app.demo.pluginclienta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.limemobile.app.plugin.PluginClientFragmentActivity;
import com.limemobile.app.plugin.internal.PluginClientManager;
import com.limemobile.app.demo.pluginclienta.R;

public class TestFragmentActivity extends PluginClientFragmentActivity
		implements OnClickListener {

	private static final String TAG = "TestFragmentActivity";

	private EditText mEditText;
	private ImageView mImageView;
	private Button mShowFragmentButton;

	private Button mStartPluginB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);
		Toast.makeText(mContext, getIntent().getStringExtra("dl_extra"),
				Toast.LENGTH_SHORT).show();
		TestButton button = (TestButton) findViewById(R.id.button1);
		button.setText(mContext.getResources().getString(R.string.test));
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "quit", Toast.LENGTH_SHORT).show();
				mContext.setResult(RESULT_FIRST_USER, new Intent());
				mContext.finish();
			}
		});

		mEditText = (EditText) findViewById(R.id.editText1);
		mEditText.setText(R.string.hello_world);
		mShowFragmentButton = (Button) findViewById(R.id.show_fragment);
		mShowFragmentButton.setOnClickListener(this);

		mStartPluginB = (Button) findViewById(R.id.start_plugin_b);
		mStartPluginB.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageResource(R.drawable.ppmm);
		Log.d(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	public void onClick(View v) {
		if (v == mShowFragmentButton) {
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.fragment_container, new TestFragment());
			transaction.addToBackStack("TestFragment#1");
			transaction.commit();
		} else if (v == mStartPluginB) {
			PluginClientManager.sharedInstance(mContext)
					.startPluginClientActivity(mContext,
							"com.limemobile.app.demo.pluginclientb",
							".MainActivity");
		}

	}

}
