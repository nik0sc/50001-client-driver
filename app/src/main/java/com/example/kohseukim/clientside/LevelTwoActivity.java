package com.example.kohseukim.clientside;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class LevelTwoActivity extends Activity {


    private static final String TAG = "RED ALERT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_two);
        Log.i(TAG, "onCreate");

        // AudioPlay.playAudio(LevelTwoActivity.this, R.raw.leveltwoalert);


        int delay = 0;
        int period = 5000;
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                AudioPlay.playAudio(LevelTwoActivity.this, R.raw.leveltwoalert);
//            }
//        }, delay, period);

        AudioPlay.playAudio(LevelTwoActivity.this, R.raw.leveltwoalert);

        final ConstraintLayout levelTwo = findViewById(R.id.LevelTwoLayout);

        // animation for blinking
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking_animation);
        levelTwo.startAnimation(startAnimation);


        levelTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Enter onClick handler");

                if (App.backend == null) {
                    Log.e(TAG, "onClick: App.backend is null, has it been initialized?");
                } else {
                    App.backend.acknowledgeAlert();
                }
                Intent i = new Intent(LevelTwoActivity.this, MapsActivity.class);
                finish();
                startActivity(i);
                AudioPlay.stopAudio();
                overridePendingTransition(0, 0);


            }
        });

    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        AudioPlay.stopAudio();



    }
}

