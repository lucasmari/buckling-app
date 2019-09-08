package com.example.bucklingcalculator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    // End Condition variable
    private double effectiveLengthFactor;

    // Material properties variables
    private double yieldStrength;
    private double elasticModulus;

    // Cross Section properties variables
    private double area;
    private double centroidalDistance;
    private double gyrationRadius;
    private double inertiaMoment;

    // EditTexts variables
    public static double length;
    private double load;
    private double eccentricity;

    // Results variables
    private double criticalStress;
    private double criticalForce;
    private double safetyFactor;

    // Material/Cross Section ArrayLists
    private ArrayList[] materials = new ArrayList[3];
    private ArrayList[] crossSections = new ArrayList[5];

    // Results ArrayList
    public static ArrayList<Double> results = new ArrayList<>();
    public static ArrayList<Double> criticalStressByLength = new ArrayList<>();
    public static ArrayList<Double> criticalForceByLength = new ArrayList<>();

    public CheckBox checkBox;
    private EditText editText3;
    private RecyclerView recyclerView;

    // Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSharedPreferences();

        // Soft keyboard configuration
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Components declaration
        ConstraintLayout layout = findViewById(R.id.layout);
        ImageView columnImageView = findViewById(R.id.columnImageView);
        Spinner spinner1 = findViewById(R.id.spinner1);
        Spinner spinner2 = findViewById(R.id.spinner2);
        Spinner spinner3 = findViewById(R.id.spinner3);
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        checkBox = findViewById(R.id.checkBox);
        editText3 = findViewById(R.id.editText3);
        Button calculateButton = findViewById(R.id.calculateButton);
        Button clearButton = findViewById(R.id.clearButton);
//        TextView introTextView = findViewById(R.id.introTextView);
        recyclerView = findViewById(R.id.resultsView);
        recyclerView.setHasFixedSize(true);
        List<Results> resultsList = Results.createResultsList(getResources());
        List<Chart> chartList = Chart.createChartList(getResources());
        ArrayList list = new ArrayList();
        list.add(resultsList.get(0));
        list.add(chartList.get(0));
        list.add(chartList.get(1));
        Log.d("LIST: ", list.get(0).toString() + list.get(1).toString());
        final ResultsAdapter resultsAdapter = new ResultsAdapter(this, list, chartList);

        ConstraintSet constraintSet1 = new ConstraintSet();
        constraintSet1.clone(this, R.layout.activity_main);
        ConstraintSet constraintSet2 = new ConstraintSet();
        constraintSet2.clone(this, R.layout.activity_main_2);

        // Materials ArrayLists initialization
        initializeMaterials();

        // Cross Sections ArrayLists initialization
        initializeCrossSection();

        // Results initialization
        initializeResults();

        // Animations
        Animation fade_out = AnimationUtils.loadAnimation(getApplication(), R.anim.fade_out);
        Animation fade_in = AnimationUtils.loadAnimation(getApplication(), R.anim.fade_in);

        // Spinner 1 adapter/listener
        setSpinnerAdapter1(spinner1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        effectiveLengthFactor = 0.9;
                        columnImageView.startAnimation(fade_out);
                        columnImageView.setImageResource(R.mipmap.img_column_fixed_fixed);
                        columnImageView.startAnimation(fade_in);
                        break;
                    case 2:
                        effectiveLengthFactor = 0.9;
                        columnImageView.startAnimation(fade_out);
                        columnImageView.setImageResource(R.mipmap.img_column_fixed_pinned);
                        columnImageView.startAnimation(fade_in);
                        break;
                    case 3:
                        effectiveLengthFactor = 2.1;
                        columnImageView.startAnimation(fade_out);
                        columnImageView.setImageResource(R.mipmap.img_column_fixed_free);
                        columnImageView.startAnimation(fade_in);
                        break;
                    case 0:
                    default:
                        effectiveLengthFactor = 1;
                        columnImageView.startAnimation(fade_out);
                        columnImageView.setImageResource(R.mipmap.img_column_pinned_pinned);
                        columnImageView.startAnimation(fade_in);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Spinner 2 adapter/listener
        setSpinnerAdapter2(spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yieldStrength = Double.valueOf(materials[1].get(position).toString());
                elasticModulus = Double.valueOf(materials[2].get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Spinner 3 adapter/listener
        setSpinnerAdapter3(spinner3);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = Double.valueOf(crossSections[1].get(position).toString());
                centroidalDistance = Double.valueOf(crossSections[2].get(position).toString());
                gyrationRadius = Double.valueOf(crossSections[3].get(position).toString());
                inertiaMoment = Double.valueOf(crossSections[4].get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Calculate button listener
        calculateButton.setOnClickListener(v -> {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (validInputs(editText1, editText2, editText3)) {

                length = Double.valueOf(editText1.getText().toString());
                load = Double.valueOf(editText2.getText().toString());
                eccentricity = Double.valueOf(editText3.getText().toString());

                setResults(resultsAdapter);

                TransitionManager.beginDelayedTransition(layout);
                constraintSet2.applyTo(layout);
            }
        });

        // Clear button listener
        clearButton.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(layout);
            constraintSet1.applyTo(layout);
            editText1.setText(getString(R.string.value_et));
            editText1.clearFocus();
            editText2.setText(getString(R.string.value_et));
            editText2.clearFocus();
            checkBox.setChecked(false);
            editText3.setText(getString(R.string.value_et));
            editText3.clearFocus();
        });

        // Checkbox listener
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkBox.isChecked())
                editText3.setEnabled(true);
            else
                editText3.setEnabled(false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                Intent intent = new Intent(this, ShareActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.export:
                verifyStoragePermissions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initializeMaterials() {
        materials[0] = new ArrayList();
        materials[0].add(getString(R.string.material_1));
        materials[0].add(getString(R.string.material_2));
        materials[1] = new ArrayList();
        materials[1].add(getString(R.string.yield_strength_1));
        materials[1].add(getString(R.string.yield_strength_2));
        materials[2] = new ArrayList();
        materials[2].add(getString(R.string.elastic_modulus_1));
        materials[2].add(getString(R.string.elastic_modulus_2));
    }

    public void initializeCrossSection() {
        crossSections[0] = new ArrayList();
        crossSections[0].add(getString(R.string.cross_section_1));
        crossSections[0].add(getString(R.string.cross_section_2));
        crossSections[1] = new ArrayList();
        crossSections[1].add(getString(R.string.area_1));
        crossSections[1].add(90E9);
        crossSections[2] = new ArrayList();
        crossSections[2].add(getString(R.string.centroidal_distance_1));
        crossSections[2].add(90E9);
        crossSections[3] = new ArrayList();
        crossSections[3].add(getString(R.string.gyration_radius_1));
        crossSections[3].add(90E9);
        crossSections[4] = new ArrayList();
        crossSections[4].add(getString(R.string.inertia_moment_1));
        crossSections[4].add(90E9);
    }

    public void initializeResults() {
        for (int i = 0; i < 3; i++) {
            results.add(0.0);
        }
    }

    // End Condition Spinner
    public void setSpinnerAdapter1(Spinner spinner1) {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.end_condition_1));
        spinnerArray.add(getResources().getString(R.string.end_condition_2));
        spinnerArray.add(getResources().getString(R.string.end_condition_3));
        spinnerArray.add(getResources().getString(R.string.end_condition_4));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, spinnerArray);
        spinner1.setAdapter(adapter);
    }

    // Material Spinner
    public void setSpinnerAdapter2(Spinner spinner2) {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.material_1));
        spinnerArray.add(getResources().getString(R.string.material_2));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, spinnerArray);
        spinner2.setAdapter(adapter);
    }

    // Cross Section Spinner
    public void setSpinnerAdapter3(Spinner spinner3) {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.cross_section_1));
        spinnerArray.add(getResources().getString(R.string.cross_section_2));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, spinnerArray);
        spinner3.setAdapter(adapter);
    }

    public boolean validInputs(EditText editText1, EditText editText2, EditText editText3) {
        if (editText1.getText().toString().equals("0") || editText1.getText().toString().equals(
                "")) {
            Toast.makeText(getApplicationContext(), "Length not set!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editText2.getText().toString().equals("0") || editText2.getText().toString().equals(
                "")) {
            Toast.makeText(getApplicationContext(), "Load not set!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editText3.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Eccentricity not set!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public double calculateCriticalStress() {
        if (checkBox.isChecked() && !editText3.getText().toString().equals("0")) {
            criticalStress =
                    (load / area) * (1 + ((eccentricity * centroidalDistance) / Math.pow(gyrationRadius, 2)) * Math.acos(((effectiveLengthFactor * length) / (2 * gyrationRadius)) * Math.sqrt(load / (area * elasticModulus))));

            for (double i = 0; i < length; i += 0.1) {
                criticalStressByLength.add((load / area) * (1 + ((eccentricity * centroidalDistance) / Math.pow(gyrationRadius, 2)) * Math.acos(((effectiveLengthFactor * i) / (2 * gyrationRadius)) * Math.sqrt(load / (area * elasticModulus)))));
            }
        }

        else {
            if (length < 1.47) {
                criticalStress =
                        yieldStrength - (Math.pow((yieldStrength * effectiveLengthFactor * length) / (2 * Math.PI * gyrationRadius), 2)) * (1 / elasticModulus);

                for (double i = 0; i < length; i += 0.1) {
                    criticalStressByLength.add(yieldStrength - (Math.pow((yieldStrength * effectiveLengthFactor * i) / (2 * Math.PI * gyrationRadius), 2)) * (1 / elasticModulus));
                }
            } else {
                criticalStress =
                        (Math.pow(Math.PI, 2) * elasticModulus) / Math.pow((effectiveLengthFactor * length /gyrationRadius), 2);

                for (double i = 0; i < 1.47; i += 0.1) {
                    criticalStressByLength.add(yieldStrength - (Math.pow((yieldStrength * effectiveLengthFactor * i) / (2 * Math.PI * gyrationRadius), 2)) * (1 / elasticModulus));
                }
                for (double i = 1.47; i < length; i += 0.1) {
                    criticalStressByLength.add((Math.pow(Math.PI, 2) * elasticModulus) / Math.pow((effectiveLengthFactor * length /gyrationRadius), 2));
                }
            }
        }

        return criticalStress;
    }

    public double calculateCriticalForce() {
        criticalForce = criticalStress * area;

        for (int i = 0; i < criticalStressByLength.size(); i++) {
            criticalForceByLength.add(criticalStressByLength.get(i) * area);
        }


        return criticalForce;
    }

    public double calculateSafetyFactor() {
        safetyFactor = criticalForce / load;

        return safetyFactor;
    }

    public void setResults(ResultsAdapter resultsAdapter) {
        results.add(0, calculateCriticalStress());
        results.add(1, calculateCriticalForce());
        results.add(2, calculateSafetyFactor());

        recyclerView.setAdapter(resultsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public static String convert(double val, int dp) {
        final String[] PREFIX_ARRAY = {"f", "p", "n", "µ", "m", "", "k", "M", "G", "T"};

        // If the value is zero, then simply return 0 with the correct number of dp
        if (val == 0) return String.format("%." + dp + "f", 0.0);

        // If the value is negative, make it positive so the log10 works
        double posVal = (val < 0) ? -val : val;
        double log10 = Math.log10(posVal);

        // Determine how many orders of 3 magnitudes the value is
        int count = (int) Math.floor(log10 / 3);

        // Calculate the index of the prefix symbol
        int index = count + 5;

        // Scale the value into the range 1<=val<1000
        val /= Math.pow(10, count * 3);

        if (index >= 0 && index < PREFIX_ARRAY.length) {
            // If a prefix exists use it to create the correct string
            return String.format("%." + dp + "f%s", val, PREFIX_ARRAY[index]);
        } else {
            // If no prefix exists just make a string of the form 000e000
            return String.format("%." + dp + "fe%d", val, count * 3);
        }
    }

    public void verifyStoragePermissions() {
        int readPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            showBottomSheetDialog();
        }
    }

    public void exportToFile() {
        Toast.makeText(this, getString(R.string.exporting_message), Toast.LENGTH_SHORT).show();

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + fileName + ".csv";
        File file = new File(filePath);
        CSVWriter writer = null;

        if (file.exists() && !file.isDirectory()) {
            FileWriter mFileWriter = null;
            try {
                mFileWriter = new FileWriter(filePath, true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.fail_reading_file_message),
                        Toast.LENGTH_SHORT).show();
            }
            writer = new CSVWriter(mFileWriter);
        } else {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.fail_creating_file_message),
                        Toast.LENGTH_SHORT).show();
            }
        }

        String[] data = {getString(R.string.critical_stress), String.valueOf(results.get(0)),
                getString(R.string.critical_force), String.valueOf(results.get(1)),
                getString(R.string.safety_factor), String.valueOf(results.get(2))};

        try {
            writer.writeNext(data);
            writer.close();
            Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.fail_writing_file_message),
                    Toast.LENGTH_SHORT).show();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Toast.makeText(this, getString(R.string.fail_path_message), Toast.LENGTH_SHORT).show();
        }

    }

    public void showBottomSheetDialog() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = this.getLayoutInflater().inflate(R.layout.fragment_bottom_sheet,
                null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        EditText editTextBottomSheet = sheetView.findViewById(R.id.editTextBottomSheet);
        Button saveButton = sheetView.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            fileName = editTextBottomSheet.getText().toString();
            exportToFile();
            mBottomSheetDialog.dismiss();
        });
    }

    public void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        switchTheme(sharedPreferences.getBoolean(getString(R.string.switch_theme_key), false));
        switchLanguage(sharedPreferences.getString(getString(R.string.drop_down_language_key),
                getString(R.string.english_value)));
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
        } else if (key.equals(getString(R.string.drop_down_language_key))) {
            switchLanguage(sharedPreferences.getString(key,
                    getString(R.string.english_value)));
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

    public void switchTheme(boolean b) {
        if (b) {
            this.setTheme(R.style.AppThemeDark);
        } else {
            this.setTheme(R.style.AppThemeLight);
        }
    }

    private void switchLanguage(Object newValue) {
        Resources res = this.getResources();

        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        if (newValue.toString().equals("Portuguese"))
            conf.setLocale(new Locale("pt", "BR"));
        else
            conf.setLocale(new Locale("en", "US"));

        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
    }
}
