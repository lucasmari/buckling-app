package com.example.bucklingcalculator;

import android.content.res.Resources;

import java.util.ArrayList;

public class Chart {

    private String name;
    private Class activityClass;

    private Chart(String name, Class activityClass) {
        this.name = name;
        this.activityClass = activityClass;
    }

    public String getName() {
        return name;
    }

    Class getActivityClass() {
        return activityClass;
    }

    static ArrayList<Chart> createChartList(Resources resources) {
        ArrayList<Chart> chartList = new ArrayList<>();

        chartList.add(new Chart(resources.getString(R.string.stress_line_chart),
                StressLineChartActivity.class));
        chartList.add(new Chart(resources.getString(R.string.force_line_chart),
                ForceLineChartActivity.class));

        return chartList;
    }
}