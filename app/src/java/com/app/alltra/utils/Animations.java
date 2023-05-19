package com.app.alltra.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.app.alltra.R;

public class Animations {

    public static void enterReveal(View myView) {
        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }

    public static void bounceIn(View view, Context context){
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce_in);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(bounceAnim);
    }

    public static void bounceOutAnim(View view, Context context) {
        if (view.getVisibility() == View.VISIBLE) {
            final Animation bounceOut = AnimationUtils.loadAnimation(context, R.anim.bounce_out);
            view.startAnimation(bounceOut);
            new Handler().postDelayed(() -> view.setVisibility(View.GONE), 200);
        }
    }
}
