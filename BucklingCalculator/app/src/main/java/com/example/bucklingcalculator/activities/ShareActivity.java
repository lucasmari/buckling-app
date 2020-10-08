package com.example.bucklingcalculator.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bucklingcalculator.R;

import static com.example.bucklingcalculator.activities.MainActivity.results;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Critical Stress: " + results.get(0) +
                "\nCritical Force: " + results.get(1) +
                "\nSafety Factor: " + results.get(2));
        this.startActivity(Intent.createChooser(shareIntent,
                this.getString(R.string.share_title)));
        finish();
    }
}
