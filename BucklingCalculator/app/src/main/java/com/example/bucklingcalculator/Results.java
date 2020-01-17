package com.example.bucklingcalculator;

import android.content.res.Resources;

import java.util.ArrayList;

import static com.example.bucklingcalculator.MainActivity.convert;
import static com.example.bucklingcalculator.MainActivity.results;

public class Results {

    private String name;

    public Results(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    static ArrayList<Results> createResultsList(Resources resources) {
        ArrayList<Results> resultsList = new ArrayList<>();

        resultsList.add(new Results(resources.getString(R.string.results_tv) + "\n\n" +
                resources.getString(R.string.critical_stress) + ": " +
                convert(results.get(0), 2) + resources.getString(R.string.stress_unit_si) +
                "\n" + resources.getString(R.string.critical_force) + ": " +
                convert(results.get(1), 2) + resources.getString(R.string.force_unit_si) +
                "\n" + resources.getString(R.string.safety_factor) + ": " +
                convert(results.get(2), 2)));

        return resultsList;
    }
}