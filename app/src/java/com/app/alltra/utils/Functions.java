package com.app.alltra.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.app.alltra.application.SettingsActivity;

public class Functions {
    /**
     * Class containing utility functions for several use cases
     */

    public static void updateConfigs(Activity activity, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        updateNightMode(activity);
        SettingsActivity.setAppLocale(activity, prefs.getString("language", ""));
    }

    public static void updateNightMode(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("night", 0);
        boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static boolean isConnectedInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
