package com.dharmshala.marulohar.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dharmshala.marulohar.R;


public class InternetConnection
/***
 Check the Internet connectivity
 ***/
{

    public static boolean checkInternet(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            Utils.showToast(activity, activity.getString(R.string.no_network_connection_alert));
            return false;

        }
        return false;
    }
}
