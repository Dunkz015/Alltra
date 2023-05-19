package com.app.alltra.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.app.alltra.R;
import com.app.alltra.application.SettingsActivity;
import com.app.alltra.backend.RandomNumberGenerator;
import com.app.alltra.utils.Dialogs;
import com.app.alltra.utils.DuoDrawerSetter;
import com.app.alltra.utils.Functions;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DuoDrawerLayout drawerLayout;
    private MeowBottomNavigation menu;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_home);

        /*----------------------------------------------------------------------------------------*/
        //Navigation menu:
        setTabBar();
        setDuoNavigationDrawer();
        menu.show(2, false);
        /*----------------------------------------------------------------------------------------*/
        TextView date = findViewById(R.id.date);
        TextView mobileData = findViewById(R.id.mobileData);
        TextView dailyUsage = findViewById(R.id.dailyUsage);
        TextView monthlyUsage = findViewById(R.id.monthlyUsage);

        date.setText(MonthRangeUtil.getCurrentMonthRange());
        mobileData.setText(RandomNumberGenerator.generateRandomInt(500, 2000) + "MB");
        dailyUsage.setText(RandomNumberGenerator.generateRandomInt(10, 150) + "MB");
        monthlyUsage.setText(RandomNumberGenerator.generateRandomInt(150, 1000) + "MB");

        setWeekGraph();
        setMonthlyUsage();
        setupPieChart();
        setupPieChart2();
        setHourlyUsage();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        Dialogs.showDemoDialog(this);
    }


    /*--------------------------------------------------------------------------------------------*/
    // Graph methods:

    private void setHourlyUsage() {
        BarChart barChart = findViewById(R.id.bar_chart_hour);

        List<BarEntry> entries = generateHourData();

        BarDataSet barDataSet = new BarDataSet(entries, "Hourly Progress");
        barDataSet.setColors(ContextCompat.getColor(this, R.color.light_red));
        barDataSet.setDrawValues(false); // Hide the value labels for all bars
        BarData barData = new BarData(barDataSet);

        // Customize the x-axis labels
        final String[] hours = {"0h", "1h", "2h", "3h", "4h", "5h", "6h", "7h", "8h", "9h", "10h", "11h", "12h", "13h",
                "14h", "15h", "16h", "17h", "18h", "19h", "20h", "21h", "22h", "23h"};

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(hours));

        // Position the x-axis at the bottom
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.text_color));

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Hide the y-axis
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        // Set x-axis scale to 1
        //barChart.getXAxis().setLabelCount(days.length);

        // Hide the grid lines
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // Enable vertical scrolling
        barChart.setScaleEnabled(true);
        barChart.setDragEnabled(true);

        // if more than 15 entries are displayed in the chart, no values will be drawn
        //barChart.setMaxVisibleValueCount(15);

        // Show value labels when a bar is clicked
        barChart.setHighlightPerTapEnabled(true);
        barChart.setHighlightPerDragEnabled(false);

        // Marker
        IMarker marker = new CustomMarkerView(this, R.layout.layout_tv_content);
        barChart.setMarker(marker);


        // Refresh the chart
        barChart.invalidate();
    }

    private void setMonthlyUsage() {
        BarChart barChart = findViewById(R.id.bar_chart);
        List<BarEntry> entries = generateData(31);

        BarDataSet barDataSet = new BarDataSet(entries, "Monthly Progress");
        barDataSet.setColors(ContextCompat.getColor(this, R.color.light_green));
        BarData barData = new BarData(barDataSet);

        // Customize the x-axis labels
        final String[] days = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
                "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));

        // Position the x-axis at the bottom
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.text_color));

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Hide the y-axis
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        // Set x-axis scale to 1
        //barChart.getXAxis().setLabelCount(days.length);

        // Hide the value labels for the bar entries
        barDataSet.setDrawValues(false);

        // Hide the grid lines
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // Enable vertical scrolling
        barChart.setScaleEnabled(true);
        barChart.setDragEnabled(true);

        // if more than 15 entries are displayed in the chart, no values will be drawn
        barChart.setMaxVisibleValueCount(15);

        // Show value labels when a bar is clicked
        barChart.setHighlightPerTapEnabled(true);
        barChart.setHighlightPerDragEnabled(false);


        // Marker
        IMarker marker = new CustomMarkerView(this, R.layout.layout_tv_content);
        barChart.setMarker(marker);

        // Refresh the chart
        barChart.invalidate();
    }

    private void setupPieChart() {
        PieChart pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);

        // Set the value label color to white
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        // Set the slice colors from resources
        int[] sliceColors = new int[] {
                ContextCompat.getColor(this, R.color.first_color),
                ContextCompat.getColor(this, R.color.second_color),
                ContextCompat.getColor(this, R.color.third_color),
                ContextCompat.getColor(this, R.color.fourth_color)
        };

        List<PieEntry> entries = generateRandomEntries();

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(sliceColors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        // Set legend text color
        Legend legend = pieChart.getLegend();
        legend.setTextColor(ContextCompat.getColor(this, R.color.text_color));

        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setupPieChart2() {
        PieChart pieChart = findViewById(R.id.pieChart2);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);

        // Set the value label color to white
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        // Set the slice colors from resources
        int[] sliceColors = new int[] {
                ContextCompat.getColor(this, R.color.third_color),
                ContextCompat.getColor(this, R.color.fourth_color),
                ContextCompat.getColor(this, R.color.second_color),
                ContextCompat.getColor(this, R.color.first_color)
        };

        List<PieEntry> entries = generateRandomEntries2();

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(sliceColors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        // Set legend text color
        Legend legend = pieChart.getLegend();
        legend.setTextColor(ContextCompat.getColor(this, R.color.text_color));

        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setWeekGraph() {
        BarChart barChart = findViewById(R.id.bar_chart_week);

        List<BarEntry> entries = generateData(7);

        Resources resources = this.getResources();
        String[] xAxisEntries = resources.getStringArray(R.array.week_days);

        BarDataSet barDataSet = new BarDataSet(entries, "Weekly Progress");
        barDataSet.setColors(ContextCompat.getColor(this, R.color.third_color));
        barDataSet.setDrawValues(false); // Hide the value labels for all bars
        BarData barData = new BarData(barDataSet);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisEntries));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.text_color));

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.setScaleEnabled(false);
        barChart.setDragEnabled(true);

        barChart.setHighlightPerTapEnabled(true);
        barChart.setHighlightPerDragEnabled(false);

        IMarker marker = new CustomMarkerView(this, R.layout.layout_tv_content);
        barChart.setMarker(marker);

        barChart.invalidate();
    }


    /*--------------------------------------------------------------------------------------------*/
    // Other methods:

    private List<BarEntry> generateHourData() {
        List<BarEntry> entries = new ArrayList<>();

        // Set usage value to zero for hours 0h to 7h
        for (int i = 0; i < 8; i++) {
            entries.add(new BarEntry(i, 0f));
        }

        // Simulate peak usage during a specific hour
        // Peak hour (6 PM)

        // Generate random data for the remaining hours
        for (int i = 8; i < 24; i++) {
            // Determine the usage value based on the hour
            float usage;
            if (i>= 18) {
                // Set peak usage value for the peak hour
                usage = getRandomUsage(80, 100); // Adjust the range as needed
            } else {
                // Set random usage values for other hours
                usage = getRandomUsage(5, 15); // Adjust the range as needed
            }

            // Add the entry to the list
            entries.add(new BarEntry(i, usage));
        }

        return entries;
    }

    private float getRandomUsage(int min, int max) {
        Random random = new Random();
        return min + random.nextInt(max - min + 1);
    }

    private List<BarEntry> generateData(int maxSize) {
        List<BarEntry> entries = new ArrayList<>();

        // Get the current day of the month
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Generate random values for each day
        Random random = new Random();
        for (int i = 0; i < maxSize; i++) {
            if (i < currentDay) {
                float value = random.nextFloat() * 100; // Random value between 0 and 100
                entries.add(new BarEntry(i, value));
            } else {
                entries.add(new BarEntry(i, 0));
            }
        }

        return entries;
    }

    private List<PieEntry> generateRandomEntries() {
        List<PieEntry> entries = new ArrayList<>();

        // Random values for the options
        Random random = new Random();
        float streamingValue = random.nextFloat() * 100;
        float internetValue = random.nextFloat() * 100;
        float videoCallValue = random.nextFloat() * 100;
        float downloadValue = random.nextFloat() * 100;

        // Add entries to the list
        entries.add(new PieEntry(streamingValue, getString(R.string.streaming)));
        entries.add(new PieEntry(internetValue, getString(R.string.internet)));
        entries.add(new PieEntry(videoCallValue, getString(R.string.video_call)));
        entries.add(new PieEntry(downloadValue, getString(R.string.download)));

        return entries;
    }

    private List<PieEntry> generateRandomEntries2() {
        List<PieEntry> entries = new ArrayList<>();

        // Random values for the options
        Random random = new Random();
        float streamingValue = random.nextFloat() * 100;
        float internetValue = random.nextFloat() * 100;
        float videoCallValue = random.nextFloat() * 100;
        float downloadValue = random.nextFloat() * 100;

        // Add entries to the list
        entries.add(new PieEntry(streamingValue, getString(R.string.images)));
        entries.add(new PieEntry(internetValue, getString(R.string.videos)));
        entries.add(new PieEntry(videoCallValue, getString(R.string.documents)));
        entries.add(new PieEntry(downloadValue, getString(R.string.music_files)));

        return entries;
    }

    /*--------------------------------------------------------------------------------------------*/
    //Tab bar menu:
    private void setTabBar() {
        final int ID_MENU = 1;
        final int ID_HOME = 2;
        final int ID_CONFIG = 3;

        menu = findViewById(R.id.tabBar);
        menu.add(new MeowBottomNavigation.Model(ID_MENU, R.drawable.ic_round_menu_24));
        menu.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_round_home_24));
        menu.add(new MeowBottomNavigation.Model(ID_CONFIG, R.drawable.ic_baseline_settings_24));
        menu.show(2, false);
        menu.bringToFront();

        menu.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case ID_MENU:
                    new Handler().postDelayed(() -> drawerLayout.openDrawer(), 300);
                    break;
                case ID_CONFIG:
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(this, SettingsActivity.class);
                        this.startActivity(intent);
                    }, 300);
                    break;
            }
            return null;
        });
    }

    /*--------------------------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        drawerLayout = findViewById(R.id.homeDrawer);
        ConstraintLayout content = findViewById(R.id.content);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
        drawerSetter.setDuoNavigationDrawer(this, menu, content);
    }

    /*--------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen()) drawerLayout.closeDrawer();
        else super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> menu.show(2, true), 200);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Location local = getCurrentLocation();
        LatLng location = new LatLng(-22.257391568321502, -45.69619112593133); // Example: San Francisco coordinates

        // Create a circle options object
        CircleOptions circleOptions = new CircleOptions()
                .center(location)
                .radius(100) // Circle radius in meters
                .strokeWidth(2)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(70, 255, 0, 0)); // Semi-transparent red fill color

        // Add the circle to the map
        Circle circle = googleMap.addCircle(circleOptions);

        // Move the camera to the circle's location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f));


        ScrollView scrollView = findViewById(R.id.scrollView);
        googleMap.setOnCameraMoveListener(() -> scrollView.requestDisallowInterceptTouchEvent(true));
        googleMap.setOnCameraIdleListener(() -> scrollView.requestDisallowInterceptTouchEvent(false));
    }


}