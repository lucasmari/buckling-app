package com.example.bucklingcalculator.models;

import android.content.res.Resources;

import com.example.bucklingcalculator.R;
import com.example.bucklingcalculator.activities.ForceLineChartActivity;
import com.example.bucklingcalculator.activities.StressLineChartActivity;

import java.util.ArrayList;

public class Chart {

    private String name;
    private Class activityClass;

    public Chart() {}

    private Chart(String name, Class activityClass) {
        this.name = name;
        this.activityClass = activityClass;
    }

    public String getName() {
        return name;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public static ArrayList<Chart> createChartList(Resources resources) {
        ArrayList<Chart> chartList = new ArrayList<>();

        chartList.add(new Chart(resources.getString(R.string.stress_line_chart),
                StressLineChartActivity.class));
        chartList.add(new Chart(resources.getString(R.string.force_line_chart),
                ForceLineChartActivity.class));

        return chartList;
    }
}