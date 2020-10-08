package com.example.bucklingcalculator.models;

import android.content.res.Resources;

import com.example.bucklingcalculator.R;

import java.util.ArrayList;

import static com.example.bucklingcalculator.activities.MainActivity.convert;
import static com.example.bucklingcalculator.activities.MainActivity.results;

public class Results {

    private String name;

    public Results(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ArrayList<Results> createResultsList(Resources resources, String stressUnit,
                                                       String forceUnit) {
        ArrayList<Results> resultsList = new ArrayList<>();

        resultsList.add(new Results(resources.getString(R.string.results_tv) + "\n\n" +
                resources.getString(R.string.critical_stress) + ": " +
                convert(results.get(0), 2) + stressUnit +
                "\n" + resources.getString(R.string.critical_force) + ": " +
                convert(results.get(1), 2) + forceUnit +
                "\n" + resources.getString(R.string.safety_factor) + ": " +
                String.format("%.2f",results.get(2))));

        return resultsList;
    }
}