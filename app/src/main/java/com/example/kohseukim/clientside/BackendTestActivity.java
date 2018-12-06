package com.example.kohseukim.clientside;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.FirebaseApp;

public class BackendTestActivity extends AppCompatActivity {

    private FrontEnd fakeFrontend;
    private BackEnd backend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FirebaseApp.initializeApp(this);

    }

    @Override
    protected void onStart() {

        fakeFrontend = new FakeFrontend();
        backend = new BackendImpl(fakeFrontend, getApplicationContext());

        backend.start("hi");

        super.onStart();
    }

    @Override
    protected void onStop() {
        backend.stop();
        super.onStop();
    }

}
