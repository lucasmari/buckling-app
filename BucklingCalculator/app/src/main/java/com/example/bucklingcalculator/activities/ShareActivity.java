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

        String data =
                getString(R.string.critical_stress) + ": " + results.get(0) + "\n" +
                getString(R.string.critical_force) + ": " + results.get(1) + "\n" +
                getString(R.string.safety_factor) + ": " + results.get(2);

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, data);
        this.startActivity(Intent.createChooser(shareIntent,
                this.getString(R.string.share_title)));
        finish();
    }
}
