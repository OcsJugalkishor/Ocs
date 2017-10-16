package com.dharmshala.marulohar.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 10/6/2017.
 */

public class PreferenceManager {
    public static void setusername(String FirstName, Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        preferences.edit().putString("firstName", FirstName).commit();
    }

    public static String getusername(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        return preferences.getString("firstName", "");
    }

    public static void setLastname(String LastName, Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        preferences.edit().putString("lastName", LastName).commit();
    }

    public static String getLastname(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        return preferences.getString("lastName", "");
    }

    public static void setEmail(String username, Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        preferences.edit().putString("email", username).commit();
    }

    public static String getEmail(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        return preferences.getString("email", "");
    }

    public static void setpassword(String password, Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        preferences.edit().putString("password", password).commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        return preferences.getString("username", "");
    }

    public static void deleteusername(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    public static void setloginstatus(Boolean isLogedIn, Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("isLogedIn", isLogedIn).commit();
    }

    public static Boolean getLoginstatus(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("Jugal", Context.MODE_PRIVATE);
        return preferences.getBoolean("isLogedIn", false);
    }
}
