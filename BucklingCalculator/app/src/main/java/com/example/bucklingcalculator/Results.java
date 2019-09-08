package com.example.bucklingcalculator;

import android.content.res.Resources;

import java.util.ArrayList;

public class Results {

    private String name;

    private Results(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    static ArrayList<Results> createResultsList(Resources resources) {
        ArrayList<Results> resultsList = new ArrayList<>();

        resultsList.add(new Results(resources.getString(R.string.results_tv)));

        return resultsList;
    }
}