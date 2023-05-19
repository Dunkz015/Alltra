package com.app.alltra.live_graph;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.alltra.R;
import com.app.alltra.app_usage.AppItem;
import com.app.alltra.app_usage.AppUsageAdapter;
import com.app.alltra.backend.RandomNumberGenerator;
import com.app.alltra.utils.Functions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LiveGraphActivity extends AppCompatActivity {

    private LineChart chart;
    private List<Entry> downloadEntries;
    private List<Entry> uploadEntries;
    private LineDataSet downloadDataSet;
    private LineDataSet uploadDataSet;

    private Handler handler;
    private Runnable dataGeneratorRunnable;
    private Random random;

    private static final int MAX_DATA_POINTS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_live_graph);

        //Toolbar
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.live_graph);
        toolbarButton.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.ic_baseline_arrow_back_24));
        toolbarButton.setOnClickListener(view -> onBackPressed());

        setListView();
        /*----------------------------------------------------------------------------------------*/
        // Graph:

        // Initialize variables
        handler = new Handler(Looper.getMainLooper());
        downloadEntries = new ArrayList<>();
        uploadEntries = new ArrayList<>();
        random = new Random();

        // Set up chart
        chart = findViewById(R.id.chart);
        setupChart();

        // Generate random data every second
        dataGeneratorRunnable = new Runnable() {
            @Override
            public void run() {
                generateRandomData();
                updateChartData();
                handler.postDelayed(this, 100);
            }
        };
        handler.post(dataGeneratorRunnable);

    }

    private void setupChart() {
        // Customize chart appearance
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Set x-axis limit
        chart.getAxisLeft().setAxisMinimum(-3f);  // Minimum value
        chart.getAxisLeft().setAxisMaximum(10f); // Maximum value

        // Set x-axis limit
        chart.getXAxis().setAxisMinimum(0f);  // Minimum value
        chart.getXAxis().setAxisMaximum(30f); // Maximum value

        // Prepare line data sets
        downloadDataSet = new LineDataSet(downloadEntries, "Download");
        uploadDataSet = new LineDataSet(uploadEntries, "Upload");

        // Set cubic bezier interpolation mode for smooth curves
        downloadDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        uploadDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Customize line data sets
        downloadDataSet.setColor(ContextCompat.getColor(this, R.color.download));
        downloadDataSet.setLineWidth(1f);
        downloadDataSet.setDrawCircleHole(false);
        downloadDataSet.setDrawValues(false);
        downloadDataSet.setDrawCircles(false);

        uploadDataSet.setColor(ContextCompat.getColor(this, R.color.upload));
        uploadDataSet.setLineWidth(1f);
        uploadDataSet.setDrawCircles(false);
        uploadDataSet.setDrawCircleHole(false);
        uploadDataSet.setDrawValues(false);

        // Add data sets to line data
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(downloadDataSet);
        dataSets.add(uploadDataSet);

        // Create line data and set it to chart
        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);

        // Refresh chart
        chart.invalidate();
    }

    @SuppressLint("SetTextI18n")
    private void generateRandomData() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#"); // Pattern for one decimal place
        TextView download = findViewById(R.id.download);
        TextView upload = findViewById(R.id.upload);
        float downloadValue;
        float uploadValue;

        // Generate random values
        if (random.nextFloat() <= 0.95) {
            downloadValue = 0f; // 0.95 of the time, set the value to 0
            uploadValue = 0f; // 0.95 of the time, set the value to 0
        } else {
            downloadValue = random.nextFloat() * 10f; // Random value between 0 and 10
            uploadValue = random.nextFloat() * 10f; // Random value between 0 and 10
            download.setText(decimalFormat.format(downloadValue) + "KB/s");
            upload.setText(decimalFormat.format(uploadValue) + "KB/s");
            //modifyRandomElementValue();
        }


        // Add new entry to download and upload entries
        downloadEntries.add(new Entry(downloadEntries.size(), downloadValue));
        uploadEntries.add(new Entry(uploadEntries.size(), uploadValue));

        // Remove old entries if their size exceeds MAX_DATA_POINTS
        if (downloadEntries.size() > MAX_DATA_POINTS) {
            downloadEntries.remove(0);
            // Update x-values of the remaining entries
            for (int i = 0; i < downloadEntries.size(); i++) {
                downloadEntries.get(i).setX(i);
            }
        }
        if (uploadEntries.size() > MAX_DATA_POINTS) {
            uploadEntries.remove(0);
            // Update x-values of the remaining entries
            for (int i = 0; i < uploadEntries.size(); i++) {
                uploadEntries.get(i).setX(i);
            }
        }
    }

    private void updateChartData() {
        // Update line data sets with new entries
        downloadDataSet.setValues(downloadEntries);
        uploadDataSet.setValues(uploadEntries);

        // Notify chart data has changed
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();

        chart.invalidate();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop data generation when the activity is destroyed
        handler.removeCallbacks(dataGeneratorRunnable);
    }



    @SuppressLint("SetTextI18n")
    private void setListView(){
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);
        List<AppItem> appItemList = new ArrayList<>();

        int count = 0;
        int maxCount = RandomNumberGenerator.generateRandomInt(6, 15);
        for (ApplicationInfo appInfo : installedApps) {
            if (count >= maxCount) {
                break;  // Limit to 7 apps
            }

            // Exclude system apps if needed
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }

            Drawable icon = appInfo.loadIcon(packageManager);
            String name = appInfo.loadLabel(packageManager).toString();

            // Create an AppItem object and add it to the list
            AppItem appItem = new AppItem(icon, name, 0, RandomNumberGenerator.generateRandomDouble(0, 15));
            appItemList.add(appItem);

            count++;
        }

        double totalValue = 0;
        for (AppItem appItem : appItemList) {
            totalValue += appItem.getValue();
        }

        int progress;
        for (AppItem appItem : appItemList) {
            progress = (int) ((100 * appItem.getValue()) / totalValue);
            appItem.setProgress(progress);
        }

        // Sort appItemList in decreasing order based on the value
        Collections.sort(appItemList, (item1, item2) -> Double.compare(item2.getValue(), item1.getValue()));

        // Create the adapter and set it to the ListView
        AppUsageAdapter adapter = new AppUsageAdapter(this, appItemList, "KB");
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    private void modifyRandomElementValue() {
        ListView listView = findViewById(R.id.list);
        AppUsageAdapter adapter = (AppUsageAdapter) listView.getAdapter();

        if (adapter != null && adapter.getCount() > 0) {
            int randomIndex = new Random().nextInt(adapter.getCount());
            AppItem randomAppItem = adapter.getItem(randomIndex);

            if (randomAppItem != null) {
                // Generate a random number between 0 and 15
                double randomValue = RandomNumberGenerator.generateRandomDouble(0, 15);

                // Update the value of the random AppItem
                randomAppItem.setValue(randomValue);

                // Calculate the new progress based on the updated value
                double totalValue = 0;
                for (int i = 0; i < adapter.getCount(); i++) {
                    totalValue += adapter.getItem(i).getValue();
                }

                int progress = (int) ((100 * randomValue) / totalValue);
                randomAppItem.setProgress(progress);

                // Reorder the list
                adapter.sortByValueDescending();

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }
        }
    }

}
