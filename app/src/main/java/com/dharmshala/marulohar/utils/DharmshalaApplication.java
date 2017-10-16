package com.dharmshala.marulohar.utils;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Jugal  on 18/08/2017.
 */

public class DharmshalaApplication extends MultiDexApplication {

    private static DharmshalaApplication dharmshalaApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();

        dharmshalaApplication = this;
    }

    public static DharmshalaApplication getInstance() {
        return dharmshalaApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(DharmshalaApplication.this);
    }
}
