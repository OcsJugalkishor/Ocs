package com.dharmshala.marulohar.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dharmshala.marulohar.MainActivity;
import com.dharmshala.marulohar.R;
import com.dharmshala.marulohar.connection.ConnectionActivity;
import com.dharmshala.marulohar.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (PreferenceManager.getLoginstatus(mContext)) {
                        System.out.println("LOGGED_IN");
                        Intent iflogged_in = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(iflogged_in);
                    }  else {
                        System.out.println("NOT_LOGGED_IN");
                        Intent notlogged_in = new Intent(SplashActivity.this, ConnectionActivity.class);
                        startActivity(notlogged_in);
                    }
                }
            }
        };
        timerThread.start();
    }
}
