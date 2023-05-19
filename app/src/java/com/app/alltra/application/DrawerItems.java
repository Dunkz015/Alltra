package com.app.alltra.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.ConfigurationCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.alltra.R;
import com.app.alltra.app_usage.AppUsageActivity;
import com.app.alltra.data_prediction.DataPredictionActivity;
import com.app.alltra.home.HomeActivity;
import com.app.alltra.live_graph.LiveGraphActivity;
import com.app.alltra.login.LoginActivity;
import com.app.alltra.speed_test.SpeedTestActivity;
import com.app.alltra.utils.Dialogs;
import com.app.alltra.utils.LottieDialog;

import java.util.Locale;

public class DrawerItems extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onDrawerItemSelected(int id, Context context) {
        Intent intent;
        try {
            if (id == R.id.home) {
                intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.logout) {
                LoginActivity.logout();

                intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.speedTest) {
                intent = new Intent(context, SpeedTestActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.appUsage) {
                intent = new Intent(context, AppUsageActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.dataPrediction) {
                intent = new Intent(context, DataPredictionActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.liveGraph) {
                intent = new Intent(context, LiveGraphActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.settings) {
                intent = new Intent(context, SettingsActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.report_bug) {
                DisplayMetrics dm = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                int height = dm.heightPixels;

                Locale current = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
                try {
                    context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:exoticnumbers.feedback@gmail.com" +
                            "?cc=" + "" +
                            "&subject=" + Uri.encode("") +
                            "&body=" + Uri.encode(
                            "Api: " + Build.VERSION.SDK_INT + "\n" +
                                    "Screen size: " + width + "x" + height + "\n" +
                                    "App locale: " + current
                    ))));
                } catch (Exception e) {
                    Toast.makeText(context, R.string.error_opening_uri_1,
                            Toast.LENGTH_LONG).show();
                }
            } else if (id == R.id.rateus) {
                Dialogs.showUnderConstructionDialog(context);
            } else if (id == R.id.about) {
                intent = new Intent(context, AboutActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.share){
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                intent.setType("text/plain");
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
