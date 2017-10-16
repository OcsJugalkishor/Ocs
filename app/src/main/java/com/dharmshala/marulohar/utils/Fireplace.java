package com.dharmshala.marulohar.utils;

import android.util.Log;

/**
 * Created by user on 9/27/2017.
 */

public class Fireplace {
    public static void i(String tag, String message){
        if(WSConsts.LOGGING_STATE.toString().equals("enabled"))
            Log.i(tag, message);
    }

    public static void d(String tag, String message){
        if(WSConsts.LOGGING_STATE.toString().equals("enabled"))
            Log.d(tag,message);
    }

    public static void w(String tag, String message){
        if(WSConsts.LOGGING_STATE.toString().equals("enabled"))
            Log.w(tag,message);
    }

    public static void e(String tag, String message){
        Log.e(tag,message);
    }
}
