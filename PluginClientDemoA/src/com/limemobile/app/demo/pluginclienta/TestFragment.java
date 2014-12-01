package com.limemobile.app.demo.pluginclienta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.limemobile.app.demo.pluginclienta.R;

public class TestFragment extends Fragment implements OnClickListener {

	private Button button1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.test_fragment, container, false);
	}

	@Override
	public void onResume() {
		button1 = (Button) (getView().findViewById(R.id.button1));
		button1.setOnClickListener(this);
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		if (v == button1) {
			Context context = getActivity();
			Intent intent = new Intent(context, MainActivity.class);
			context.startActivity(intent);
		}

	}

}
