package com.example.bucklingcalculator.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.bucklingcalculator.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.bucklingcalculator.activities.MainActivity.criticalForceByLength;
import static com.example.bucklingcalculator.activities.MainActivity.length;

public class ForceLineChartActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Cartesian cartesian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        setTitle(R.string.force_chart_title);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.yAxis(0).title(getString(R.string.force_axis_si));
        cartesian.xAxis(0).title(getString(R.string.length_axis_si));
        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title(getString(R.string.force_line_chart));

        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        int j = 0;
        for (double i = 0; i < 4; i += 0.1) {
            seriesData.add(new CustomDataEntry(String.valueOf(i),criticalForceByLength.get(j)));
            j++;
        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name(getString(R.string.force_curve));
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);

        setupSharedPreferences();
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value) {
            super(x, value);
//            setValue("value2", value2);
        }

    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        switchUnit(sharedPreferences.getString(getString(R.string.drop_down_units_key), "SI"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.switch_theme_key))) {
            switchTheme(sharedPreferences.getBoolean(key, false));
            recreate();
        } else if (key.equals(getString(R.string.drop_down_units_key))) {
            switchUnit(sharedPreferences.getString(key, "SI"));
            recreate();
        }
    }

    @Override
    public final void setTheme(final int resid) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme = sharedPreferences.getBoolean(getString(R.string.switch_theme_key), false);

        if (theme) {
            super.setTheme(R.style.AppThemeDark);
        } else {
            super.setTheme(R.style.AppThemeLight);
        }
    }

    private void switchTheme(boolean b) {
        if (b) {
            this.setTheme(R.style.AppThemeDark);
        } else {
            this.setTheme(R.style.AppThemeLight);
        }
    }

    private void switchUnit(String newValue) {
        if (newValue.equals(getResources().getString(R.string.us_value))) {
            cartesian.yAxis(0).title(getString(R.string.force_axis_us));
            cartesian.xAxis(0).title(getString(R.string.length_axis_us));
        }
        else {
            cartesian.yAxis(0).title(getString(R.string.force_axis_si));
            cartesian.xAxis(0).title(getString(R.string.length_axis_si));
        }
    }
}