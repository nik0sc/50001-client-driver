package com.example.kohseukim.clientside;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class LevelOneActivity extends Activity {
    public static final String TAG = "LevelOneActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_one);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((width), (int)(height*0.3));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;

        getWindow().setAttributes(params);

        final FrameLayout mainLayout = findViewById(R.id.pop_layout);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(LevelOneActivity.this, MapsActivity.class);
                startActivity(i);

            }
        }, 5000);

//        mainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: Enter onClick handler");
//
//                if (App.backend == null) {
//                    Log.e(TAG, "onClick: App.backend is null, has it been initialized?");
//                } else {
//                    App.backend.acknowledgeAlert();
//                }
//
//                Intent i = new Intent(LevelOneActivity.this, MapsActivity.class);
//                startActivity(i);
//
//            }
//        });
    }

}
