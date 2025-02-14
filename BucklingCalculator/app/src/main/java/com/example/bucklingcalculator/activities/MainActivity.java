package com.example.bucklingcalculator.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.TransitionManager;
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

import com.example.bucklingcalculator.R;
import com.example.bucklingcalculator.adapters.ResultsAdapter;
import com.example.bucklingcalculator.models.Chart;
import com.example.bucklingcalculator.models.Materials;
import com.example.bucklingcalculator.models.Results;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.opencsv.CSVWriter;
import com.tobiasschuerg.prefixsuffix.PrefixSuffixEditText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    // EditTexts variables
    public static double length = 0;
    private double load = 0;
    private double eccentricity = 0;

    // Results variables
    private double criticalStress;
    private double criticalForce;
    private double safetyFactor;

    // Material/Cross Section ArrayLists
    public static ArrayList<String>[] materials = new ArrayList[3];
    public static ArrayList<String> materialsProperties = new ArrayList();
    public static ArrayList<String>[] crossSections = new ArrayList[5];
    public static ArrayList<String> crossSectionsProperties = new ArrayList();

    // Results ArrayList
    public static ArrayList<Double> results = new ArrayList<>();
    public static ArrayList<Double> criticalStressByLength = new ArrayList<>();
    public static ArrayList<Double> criticalForceByLength = new ArrayList<>();

    private ImageView columnImageView;
    private CheckBox checkBox;
    private PrefixSuffixEditText editText1;
    private PrefixSuffixEditText editText2;
    private PrefixSuffixEditText editText3;
    private RecyclerView recyclerView;
    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;

    // Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String fileName = "";
    private boolean theme;
    private boolean usSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        // Soft keyboard configuration
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Components declaration
        ConstraintLayout layout = findViewById(R.id.layout);
        columnImageView = findViewById(R.id.columnImageView);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        editText1 = findViewById(R.id.editText1);
        editText1.setSuffix(" m");
        editText2 = findViewById(R.id.editText2);
        editText2.setSuffix(" N");
        checkBox = findViewById(R.id.checkBox);
        editText3 = findViewById(R.id.editText3);
        editText3.setSuffix(" m");
        Button calculateButton = findViewById(R.id.calculateButton);
        Button clearButton = findViewById(R.id.clearButton);
        recyclerView = findViewById(R.id.resultsView);

        // Layouts constraints
        ConstraintSet constraintSet1 = new ConstraintSet();
        constraintSet1.clone(this, R.layout.activity_main);
        ConstraintSet constraintSet2 = new ConstraintSet();
        constraintSet2.clone(this, R.layout.activity_main_2);

        // Materials ArrayLists initialization
        initializeMaterials();
        initializeMaterialsProperties();

        // Cross Sections ArrayLists initialization
        initializeCrossSections();
        initializeCrossSectionsProperties();

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
                        if (theme)
                            columnImageView.setImageResource(R.mipmap.img_column_fixed_fixed_dark);
                        else
                            columnImageView.setImageResource(R.mipmap.img_column_fixed_fixed);
                        columnImageView.startAnimation(fade_in);
                        break;
                    case 2:
                        effectiveLengthFactor = 0.9;
                        columnImageView.startAnimation(fade_out);
                        if (theme)
                            columnImageView.setImageResource(R.mipmap.img_column_fixed_pinned_dark);
                        else
                            columnImageView.setImageResource(R.mipmap.img_column_fixed_pinned);
                        columnImageView.startAnimation(fade_in);
                        break;
                    case 3:
                        effectiveLengthFactor = 2.1;
                        columnImageView.startAnimation(fade_out);
                        if (theme)
                            columnImageView.setImageResource(R.mipmap.img_column_fixed_free_dark);
                        else
                            columnImageView.setImageResource(R.mipmap.img_column_fixed_free);
                        columnImageView.startAnimation(fade_in);
                        break;
                    case 0:
                    default:
                        effectiveLengthFactor = 1;
                        columnImageView.startAnimation(fade_out);
                        if (theme)
                            columnImageView.setImageResource(R.mipmap.img_column_pinned_pinned_dark);
                        else
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
                yieldStrength = Double.parseDouble(materials[1].get(position));
                elasticModulus = Double.parseDouble(materials[2].get(position));
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
                area = Double.parseDouble(crossSections[1].get(position));
                centroidalDistance = Double.parseDouble(crossSections[2].get(position));
                gyrationRadius = Double.parseDouble(crossSections[3].get(position));
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
                results.clear();
                criticalStressByLength.clear();
                criticalForceByLength.clear();

                length = Double.parseDouble(editText1.getText().toString());
                load = Double.parseDouble(editText2.getText().toString());
                if (checkBox.isChecked()) eccentricity = Double.parseDouble(editText3.getText().toString());

                setResults();

                TransitionManager.beginDelayedTransition(layout);
                constraintSet2.applyTo(layout);
            }
        });

        // Clear button listener
        clearButton.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(layout);
            constraintSet1.applyTo(layout);
            editText1.clearFocus();
            editText1.setText("");
            editText2.clearFocus();
            editText2.setText("");
            checkBox.setChecked(false);
            editText3.clearFocus();
            editText3.setText("");
        });

        // Checkbox listener
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editText3.setEnabled(checkBox.isChecked());
        });

        setupSharedPreferences();
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
            case R.id.materials:
                intent = new Intent(this, MaterialsActivity.class);
                startActivity(intent);
                return true;
            case R.id.cross_sections:
                intent = new Intent(this, CrossSectionsActivity.class);
                startActivity(intent);
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

    public void initializeMaterialsProperties() {
        materialsProperties.add(getString(R.string.yield_strength_title));
        materialsProperties.add(getString(R.string.elastic_modulus_title));
    }

    public void initializeCrossSections() {
        crossSections[0] = new ArrayList();
        crossSections[0].add(getString(R.string.cross_section_1));
        crossSections[0].add(getString(R.string.cross_section_2));
        crossSections[1] = new ArrayList();
        crossSections[1].add(getString(R.string.area_1));
        crossSections[1].add(getString(R.string.area_2));
        crossSections[2] = new ArrayList();
        crossSections[2].add(getString(R.string.centroidal_distance_1));
        crossSections[2].add(getString(R.string.centroidal_distance_2));
        crossSections[3] = new ArrayList();
        crossSections[3].add(getString(R.string.gyration_radius_1));
        crossSections[3].add(getString(R.string.gyration_radius_2));
    }

    public void initializeCrossSectionsProperties() {
        crossSectionsProperties.add(getString(R.string.area_title));
        crossSectionsProperties.add(getString(R.string.centroidal_distance_title));
        crossSectionsProperties.add(getString(R.string.gyration_radius_title));
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

    public void reloadSpinnerAdapter2(Spinner spinner2) {
        List<String> spinnerArray = new ArrayList<>();
        for (int i = 0; i < materials[0].size(); i++) {
            spinnerArray.add(materials[0].get(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, spinnerArray);
        spinner2.setAdapter(adapter);
    }

    public void reloadSpinnerAdapter3(Spinner spinner3) {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.addAll(crossSections[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, spinnerArray);
        spinner3.setAdapter(adapter);
    }

    public boolean validInputs(EditText editText1, EditText editText2, EditText editText3) {
        if (editText1.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.length_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editText2.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.load_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editText3.getText().toString().equals("") && checkBox.isChecked()) {
            Toast.makeText(getApplicationContext(), getString(R.string.eccentricity_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public double calculateTransitionSlendernessRatio() {
        return Math.sqrt((2 * Math.pow(Math.PI, 2) * elasticModulus) / (Math.pow(effectiveLengthFactor, 2) * yieldStrength));
    }

    public double calculateTransitionLength() {
        return calculateTransitionSlendernessRatio() * gyrationRadius;
    }

    public double calculateCriticalStress() {
        if (checkBox.isChecked()) {
            double x =
                    ((effectiveLengthFactor * length) / (2 * gyrationRadius)) * Math.sqrt(load / (area * elasticModulus));

            Log.d("cos", String.valueOf(x));
            // Secant equation
            criticalStress =
                    (load / area) * (1 + ((eccentricity * centroidalDistance) / Math.pow(gyrationRadius, 2)) * (1 / Math.cos(x)));

            for (double i = 0; i <= 4; i += 0.1) {
                criticalStressByLength.add((load / area) * (1 + ((eccentricity * centroidalDistance) / Math.pow(gyrationRadius, 2)) * (1 / Math.cos(x))));
            }
        } else {
            if (length < calculateTransitionLength()) {
                // Johnson equation
                criticalStress =
                        yieldStrength - (Math.pow((yieldStrength * effectiveLengthFactor * length) / (2 * Math.PI * gyrationRadius), 2)) * (1 / elasticModulus);
            } else {
                // Euler equation
                criticalStress =
                        (Math.pow(Math.PI, 2) * elasticModulus) / Math.pow((effectiveLengthFactor * length / gyrationRadius), 2);
            }

            // Data for the chart
            for (double i = 0; i < calculateTransitionLength(); i += 0.1) {
                criticalStressByLength.add(yieldStrength - (Math.pow((yieldStrength * effectiveLengthFactor * i) / (2 * Math.PI * gyrationRadius), 2)) * (1 / elasticModulus));
            }
            for (double i = calculateTransitionLength(); i <= 4; i += 0.1) {
                criticalStressByLength.add((Math.pow(Math.PI, 2) * elasticModulus) / Math.pow((effectiveLengthFactor * i / gyrationRadius), 2));
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

    public void setResults() {
        results.add(0, calculateCriticalStress());
        results.add(1, calculateCriticalForce());
        results.add(2, calculateSafetyFactor());

        List<Results> resultsList;
        if (usSystem) resultsList = Results.createResultsList(getResources(),
                getString(R.string.stress_unit_us), getString(R.string.force_unit_us));
        else resultsList = Results.createResultsList(getResources(),
                getString(R.string.stress_unit_si), getString(R.string.force_unit_si));
        List<Chart> chartList = Chart.createChartList(getResources());
        ArrayList list = new ArrayList();
        list.add(resultsList.get(0));
        list.add(chartList.get(0));
        list.add(chartList.get(1));
        final ResultsAdapter resultsAdapter = new ResultsAdapter(this, list, resultsList,
                chartList);

        recyclerView.setHasFixedSize(true);
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
            return String.format("%." + dp + "f %s", val, PREFIX_ARRAY[index]);
        } else {
            // If no prefix exists just make a string of the form 000e000
            return String.format("%." + dp + "fe %d", val, count * 3);
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

    @Override
    protected void onResume() {
        super.onResume();
        reloadSpinnerAdapter2(spinner2);
        reloadSpinnerAdapter3(spinner3);
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
        theme = sharedPreferences.getBoolean(getString(R.string.switch_theme_key), false);

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
            editText1.setSuffix(" in");
            editText2.setSuffix(" lbf");
            editText3.setSuffix(" in");
            usSystem = true;
        }
        else {
            editText1.setSuffix(" m");
            editText2.setSuffix(" N");
            editText3.setSuffix(" m");
            usSystem = false;
        }
    }
}
