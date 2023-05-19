package com.app.alltra.data_prediction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.alltra.R;
import com.app.alltra.backend.RandomNumberGenerator;
import com.app.alltra.utils.Functions;

import java.util.Calendar;

public class DataPredictionActivity extends AppCompatActivity {

    private static final int USAGE = RandomNumberGenerator.generateRandomInt(100, 2000);
    private static final int DAYS = RandomNumberGenerator.generateRandomInt(1, getRemainingDaysOfMonth());
    private static final int USAGE_STATUS = RandomNumberGenerator.generateRandomInt(0, 1);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_data_prediction);

        //Toolbar
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.data_prediction);
        toolbarButton.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.ic_baseline_arrow_back_24));
        toolbarButton.setOnClickListener(view -> onBackPressed());

        /*----------------------------------------------------------------------------------------*/
        LinearLayout mobile_plan_review;
        LinearLayout data_saving;
        TextView mobile_plan_review_value;
        TextView days = findViewById(R.id.days);

        if(USAGE_STATUS == 1){
            //High usage
            mobile_plan_review = findViewById(R.id.mobile_plan_negative_review);
            data_saving = findViewById(R.id.data_saving_high);
            mobile_plan_review_value = findViewById(R.id.upgrade);
            mobile_plan_review_value.setText("+"+USAGE+"MB");

        }else{
            //Low usage
            mobile_plan_review = findViewById(R.id.mobile_plan_positive_review);
            data_saving = findViewById(R.id.data_saving_low);
            mobile_plan_review_value = findViewById(R.id.downgrade);
            mobile_plan_review_value.setText("-"+USAGE+"MB");
        }
        mobile_plan_review.setVisibility(View.VISIBLE);
        data_saving.setVisibility(View.VISIBLE);
        days.setText(String.valueOf(DAYS));

    }

    /*--------------------------------------------------------------------------------------------*/

    public static int getRemainingDaysOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return totalDaysInMonth - currentDayOfMonth;
    }
}
