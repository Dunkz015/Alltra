package com.app.alltra.speed_test;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.alltra.R;
import com.app.alltra.backend.RandomNumberGenerator;
import com.app.alltra.utils.Animations;
import com.app.alltra.utils.Dialogs;
import com.app.alltra.utils.Functions;

import java.util.concurrent.atomic.AtomicInteger;

import me.ibrahimsn.lib.Speedometer;

public class SpeedTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_speed_test);

        //Toolbar
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.speed_test);
        toolbarButton.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.ic_baseline_arrow_back_24));
        toolbarButton.setOnClickListener(view -> onBackPressed());

        Button measureButton = findViewById(R.id.measure);
        measureButton.setEnabled(true);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        measureButton.setOnClickListener(v -> {
            measureButton.startAnimation(bounceAnim);
            measureButton.setEnabled(false);

            new Handler().postDelayed(() -> {
                if(Functions.isConnectedInternet(this)) {
                   //measureReal();
                   measureRandom();
                }
                else
                    Dialogs.showConnectionErrorDialog(this);
            }, 200);

        });
    }

    private void measureRandom(){
        Button measureButton = findViewById(R.id.measure);
        TextView download = findViewById(R.id.download);
        TextView upload = findViewById(R.id.upload);
        TextView ping = findViewById(R.id.ping);
        Speedometer speedometer = findViewById(R.id.speedometer);

        int delay = 2100;
        AtomicInteger value = new AtomicInteger();

        //Download
        speedometer.setMetricText("Mbps");
        showLoadingDownload();
        speedometer.setSpeed(RandomNumberGenerator.generateRandomInt(1, 100), 2000L, null);
        new Handler().postDelayed(() -> speedometer.setSpeed(RandomNumberGenerator.generateRandomInt(1, 100), 2000L, null), delay);
        new Handler().postDelayed(() -> {
            value.set(RandomNumberGenerator.generateRandomInt(1, 100));
            speedometer.setSpeed(value.get(), 2000L, null);
        }, delay*2);
        new Handler().postDelayed(() -> {
            speedometer.setSpeed(0, 2000L, null);
            download.setText(String.valueOf(value.get()));

            hideLoadingDownload();
            showLoadingUpload();
        }, delay*3);

        //Upload
        new Handler().postDelayed(() -> speedometer.setSpeed(RandomNumberGenerator.generateRandomInt(1, 100), 2000L, null), delay*4);
        new Handler().postDelayed(() -> {
            value.set(RandomNumberGenerator.generateRandomInt(1, 100));
            speedometer.setSpeed(value.get(), 2000L, null);
        }, delay*5);
        new Handler().postDelayed(() -> {
            speedometer.setSpeed(0, 2000L, null);
            upload.setText(String.valueOf(value.get()));
            speedometer.setMetricText("ms");

            hideLoadingUpload();
            showLoadingPing();
        }, delay*6);

        //Ping
        new Handler().postDelayed(() -> speedometer.setSpeed(RandomNumberGenerator.generateRandomInt(1, 20), 2000L, null), delay*7);
        new Handler().postDelayed(() -> {
            value.set(RandomNumberGenerator.generateRandomInt(1, 20));
            speedometer.setSpeed(value.get(), 2000L, null);
        }, delay*8);
        new Handler().postDelayed(() -> {
            speedometer.setSpeed(0, 2000L, null);
            ping.setText(String.valueOf(value.get()));

            hideLoadingPing();
            measureButton.setEnabled(true);
        }, delay*9);

    }

    private void showLoadingDownload(){
        TextView download = findViewById(R.id.download);
        LottieAnimationView loadingDownload = findViewById(R.id.loadingDownload);

        Animations.bounceOutAnim(download, this);
        new Handler().postDelayed(() -> Animations.bounceIn(loadingDownload, this), 50);
    }

    private void hideLoadingDownload(){
        TextView download = findViewById(R.id.download);
        LottieAnimationView loadingDownload = findViewById(R.id.loadingDownload);

        Animations.bounceOutAnim(loadingDownload, this);
        new Handler().postDelayed(() -> Animations.bounceIn(download, this), 50);
    }

    private void showLoadingUpload(){
        TextView upload = findViewById(R.id.upload);
        LottieAnimationView loadingUpload = findViewById(R.id.loadingUpload);

        Animations.bounceOutAnim(upload, this);
        new Handler().postDelayed(() -> Animations.bounceIn(loadingUpload, this), 50);
    }

    private void hideLoadingUpload(){
        TextView upload = findViewById(R.id.upload);
        LottieAnimationView loadingUpload = findViewById(R.id.loadingUpload);

        Animations.bounceOutAnim(loadingUpload, this);
        new Handler().postDelayed(() -> Animations.bounceIn(upload, this), 50);
    }

    private void showLoadingPing(){
        TextView ping = findViewById(R.id.ping);
        LottieAnimationView loadingPing = findViewById(R.id.loadingPing);

        Animations.bounceOutAnim(ping, this);
        new Handler().postDelayed(() -> Animations.bounceIn(loadingPing, this), 50);
    }

    private void hideLoadingPing(){
        TextView ping = findViewById(R.id.ping);
        LottieAnimationView loadingPing = findViewById(R.id.loadingPing);

        Animations.bounceOutAnim(loadingPing, this);
        new Handler().postDelayed(() -> Animations.bounceIn(ping, this), 50);
    }


}
