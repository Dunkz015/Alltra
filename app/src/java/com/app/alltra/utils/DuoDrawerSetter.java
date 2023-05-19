package com.app.alltra.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.app.alltra.R;
import com.app.alltra.application.DrawerItems;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DuoDrawerSetter implements View.OnClickListener{
    private final DuoDrawerLayout drawerLayout;
    private final Context context;

    public DuoDrawerSetter(DuoDrawerLayout drawerLayout, Context context) {
        this.drawerLayout = drawerLayout;
        this.context = context;
    }

    public void setDuoNavigationDrawer(Activity activity, MeowBottomNavigation menu, View content) {
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(activity, drawerLayout, null,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerToggle.setDrawerIndicatorEnabled(false);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        View menuView = drawerLayout.getMenuView();

        TextView home = menuView.findViewById(R.id.home);
        TextView settings = menuView.findViewById(R.id.settings);
        TextView about = menuView.findViewById(R.id.about);
        TextView bug = menuView.findViewById(R.id.report_bug);
        TextView share = menuView.findViewById(R.id.share);
        TextView rateUs = menuView.findViewById(R.id.rateus);
        TextView logout = menuView.findViewById(R.id.logout);
        TextView speedTest = menuView.findViewById(R.id.speedTest);
        TextView appUsage = menuView.findViewById(R.id.appUsage);
        TextView liveGraph = menuView.findViewById(R.id.liveGraph);
        TextView dataPrediction = menuView.findViewById(R.id.dataPrediction);

        home.setOnClickListener(this);
        settings.setOnClickListener(this);
        about.setOnClickListener(this);
        bug.setOnClickListener(this);
        share.setOnClickListener(this);
        rateUs.setOnClickListener(this);
        logout.setOnClickListener(this);
        speedTest.setOnClickListener(this);
        appUsage.setOnClickListener(this);
        liveGraph.setOnClickListener(this);
        dataPrediction.setOnClickListener(this);

        LottieAnimationView animationView = menuView.findViewById(R.id.animationView);
        LottieDialog.playLottieOnce(animationView);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_first));
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Respond when the drawer is opened
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_first));
                if (!menu.isShowing(1))
                    menu.show(1, true);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Respond when the drawer is closed
                content.setBackground(ContextCompat.getDrawable(context,
                        R.color.main_background_color));
                menu.show(2, true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Respond when the drawer motion state changes
            }
        });
    }

    private void startTransformationAnimation(boolean open, ImageButton menuButton) {
        //Create rotation animator
        float start = open ? 180 : 0;
        float end = open ? 0 : 180;
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(menuButton, "rotation", start, end);
        rotateAnimator.setDuration(200);

        // Create icon change animator
        Drawable arrowIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_arrow_back_24);
        Drawable hamburgerIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_menu_24);

        // Play both animators together
        rotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(open)
                    menuButton.setImageDrawable(arrowIcon);
                else
                    menuButton.setImageDrawable(hamburgerIcon);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        rotateAnimator.start();
    }

    private void startTransformationAnimationCalc(boolean open, ImageButton menuButton) {
        //Create rotation animator
        float start = open ? 180 : 0;
        float end = open ? 0 : 180;
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(menuButton, "rotation", start, end);
        rotateAnimator.setDuration(200);

        // Create icon change animator
        Drawable arrowIcon = ContextCompat.getDrawable(context, R.drawable.ic_round_arrow_back_24);
        Drawable hamburgerIcon = ContextCompat.getDrawable(context, R.drawable.ic_round_menu_12);

        // Play both animators together
        rotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (open)
                    menuButton.setImageDrawable(arrowIcon);
                else
                    menuButton.setImageDrawable(hamburgerIcon);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        rotateAnimator.start();
    }

    @Override
    public void onClick(View view) {
        drawerLayout.closeDrawer();
        DrawerItems drawerItems = new DrawerItems();
        drawerItems.onDrawerItemSelected(view.getId(), context);
    }

}
