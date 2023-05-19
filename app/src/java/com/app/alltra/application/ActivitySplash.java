package com.app.alltra.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.app.alltra.R;
import com.app.alltra.login.LoginActivity;
import com.app.alltra.utils.Functions;

public class ActivitySplash extends Activity {
    // Timer da splash screen
    private static final int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        ImageView logo = findViewById(R.id.logo);

        logo.setVisibility(View.VISIBLE);
        logo.startAnimation(bounceAnim);

        // Preload the HomeActivity intent
        Intent intent = new Intent(this, LoginActivity.class);

        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
