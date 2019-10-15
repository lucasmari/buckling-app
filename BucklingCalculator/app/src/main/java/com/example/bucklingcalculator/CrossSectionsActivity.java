package com.example.bucklingcalculator;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import static com.example.bucklingcalculator.MainActivity.crossSections;

public class CrossSectionsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static CrossSectionsAdapter crossSectionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_sections);
        setTitle(R.string.cross_sections_option);
        setupSharedPreferences();

        RecyclerView recyclerView = findViewById(R.id.list);
        crossSectionsAdapter = new CrossSectionsAdapter(CrossSections.ITEMS, getSupportFragmentManager());

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(crossSectionsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddDialog());
    }

    public static class AddDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_add_cross_section, container, false);
            EditText editText1 = view.findViewById(R.id.dialogEditText1);
            EditText editText2 = view.findViewById(R.id.dialogEditText2);
            EditText editText3 = view.findViewById(R.id.dialogEditText3);
            EditText editText4 = view.findViewById(R.id.dialogEditText4);
            EditText editText5 = view.findViewById(R.id.dialogEditText5);
            Button saveButton = view.findViewById(R.id.dialogSaveButton);
            saveButton.setOnClickListener(v -> {
                crossSections[0].add(editText1.getText().toString());
                crossSections[1].add(editText2.getText().toString());
                crossSections[2].add(editText3.getText().toString());
                crossSections[3].add(editText4.getText().toString());
                crossSections[4].add(editText5.getText().toString());
                crossSectionsAdapter.addItem(CrossSections.createCrossSection(crossSections[0].size()-1),
                        crossSections[0].size()-1);
                AddDialogFragment.this.dismiss();
            });
            Button cancelButton = view.findViewById(R.id.dialogCancelButton);
            cancelButton.setOnClickListener(v -> AddDialogFragment.this.getDialog().cancel());

            return view;
        }
    }

    public void showAddDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddDialogFragment();
        dialog.show(this.getSupportFragmentManager(), "AddDialogFragment");
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        switchTheme(sharedPreferences.getBoolean(getString(R.string.switch_theme_key), false));
        switchLanguage(sharedPreferences.getString(getString(R.string.drop_down_language_key),
                getResources().getString(R.string.english_value)));
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
                    getResources().getString(R.string.english_value)));
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

    private void switchLanguage(Object newValue) {
        Resources res = this.getResources();

        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        if (newValue.toString().equals(getResources().getString(R.string.portuguese_value)))
            conf.setLocale(new Locale("pt", "BR"));
        else
            conf.setLocale(new Locale("en", "US"));

        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
    }
}
