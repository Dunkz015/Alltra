package com.app.alltra.app_usage;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.alltra.R;
import com.app.alltra.backend.RandomNumberGenerator;
import com.app.alltra.home.MonthRangeUtil;
import com.app.alltra.utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AppUsageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_app_usage);

        //Toolbar
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.app_usage);
        toolbarButton.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.ic_baseline_arrow_back_24_white));
        toolbarButton.setOnClickListener(view -> onBackPressed());

        /*----------------------------------------------------------------------------------------*/
        TextView date = findViewById(R.id.date);
        date.setText(MonthRangeUtil.getCurrentMonthRange());

        setListView();

    }

    @SuppressLint("SetTextI18n")
    private void setListView(){
        TextView dailyUsage = findViewById(R.id.dailyUsage);
        TextView monthlyUsage = findViewById(R.id.monthlyUsage);
        TextView mobileData = findViewById(R.id.mobileData);

        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);
        List<AppItem> appItemList = new ArrayList<>();

        int count = 0;
        int maxCount = RandomNumberGenerator.generateRandomInt(6, 20);
        for (ApplicationInfo appInfo : installedApps) {
            if (count >= maxCount) {
                break;
            }

            // Exclude system apps if needed
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }

            Drawable icon = appInfo.loadIcon(packageManager);
            String name = appInfo.loadLabel(packageManager).toString();

            // Create an AppItem object and add it to the list
            AppItem appItem = new AppItem(icon, name, 0, RandomNumberGenerator.generateRandomDouble(0, 200));
            appItemList.add(appItem);

            count++;
        }

        double totalValue = 0;
        for (AppItem appItem : appItemList) {
            totalValue += appItem.getValue();
        }
        dailyUsage.setText(((int) totalValue/31 )+ "MB");
        monthlyUsage.setText(((int) (totalValue/31)*12 )+ "MB");
        mobileData.setText((int) (totalValue)+ "MB");

        int progress;
        for (AppItem appItem : appItemList) {
            progress = (int) ((100 * appItem.getValue()) / totalValue);
            appItem.setProgress(progress);
        }

        // Sort appItemList in decreasing order based on the value
        Collections.sort(appItemList, (item1, item2) -> Double.compare(item2.getValue(), item1.getValue()));

        // Create the adapter and set it to the ListView
        AppUsageAdapter adapter = new AppUsageAdapter(this, appItemList, "MB");
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

}
