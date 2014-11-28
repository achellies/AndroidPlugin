package com.limemobile.app.demo.pluginclienta;

import com.limemobile.app.plugin.PluginClientActivity;
import com.limemobile.app.demo.pluginclienta.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends PluginClientActivity {

    private static final String TAG = "Client-MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        //that.setContentView(generateContentView(that));
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "you clicked button",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, TestFragmentActivity.class);
                intent.putExtra("dl_extra", "from DL framework");
                startActivityForResult(intent, 0);
            }
            
        });
    }

    private View generateContentView(final Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        layout.setBackgroundColor(Color.parseColor("#F79AB5"));
        Button button = new Button(context);
        button.setText("Start TestActivity");
        layout.addView(button, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked button",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, TestFragmentActivity.class);
                intent.putExtra("dl_extra", "from DL framework");
                startActivityForResult(intent, 0);
            }
        });
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
