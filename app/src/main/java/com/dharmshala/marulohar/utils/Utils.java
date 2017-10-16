package com.dharmshala.marulohar.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by user on 9/26/2017.
 */

public class Utils {
    public static final int SHORT_DELAY = 1000;

    public static void showToast(Activity activity, String message) {
        try {
            final Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, SHORT_DELAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initToast(Context c, String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }
}
