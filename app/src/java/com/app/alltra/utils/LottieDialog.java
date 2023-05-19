package com.app.alltra.utils;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.app.alltra.R;

public class LottieDialog {
    private static AlertDialog dialog;

    @SuppressLint("InflateParams")
    public static void startLoadingDialog1(Activity activity){
        if(dialog != null && dialog.isShowing())
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.lottie_loading_1, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void startDialog(Activity activity, int layout){
        if(dialog != null && dialog.isShowing())
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(layout, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        new Handler().postDelayed(LottieDialog::dismissDialog, 2000);
    }

    public static void playLottieOnce(LottieAnimationView animationView){
        animationView.setOnClickListener(v -> {
            // Start the animation
            animationView.playAnimation();
        });


    }
    public static void dismissDialog(){
        dialog.dismiss();
    }
}