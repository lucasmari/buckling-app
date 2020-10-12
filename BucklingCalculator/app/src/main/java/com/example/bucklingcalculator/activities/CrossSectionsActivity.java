package com.example.bucklingcalculator.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.bucklingcalculator.R;
import com.example.bucklingcalculator.adapters.CrossSectionsAdapter;
import com.example.bucklingcalculator.models.CrossSections;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.bucklingcalculator.activities.MainActivity.crossSections;

public class CrossSectionsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static CrossSectionsAdapter crossSectionsAdapter;

    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;

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

    private class AddDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_add_cross_section, container, false);

            editText1 = view.findViewById(R.id.dialogEditText1);
            editText2 = view.findViewById(R.id.dialogEditText2);
            editText3 = view.findViewById(R.id.dialogEditText3);
            editText4 = view.findViewById(R.id.dialogEditText4);
            editText5 = view.findViewById(R.id.dialogEditText5);

            Button saveButton = view.findViewById(R.id.dialogSaveButton);
            saveButton.setOnClickListener(v -> {
                saveCrossSection();
                AddDialogFragment.this.dismiss();
            });

            Button cancelButton = view.findViewById(R.id.dialogCancelButton);
            cancelButton.setOnClickListener(v -> AddDialogFragment.this.getDialog().cancel());

            return view;
        }
    }

    private void saveCrossSection() {
        crossSections[0].add(editText1.getText().toString());
        crossSections[1].add(editText2.getText().toString());
        crossSections[2].add(editText3.getText().toString());
        crossSections[3].add(editText4.getText().toString());
        crossSections[4].add(editText5.getText().toString());
        crossSectionsAdapter.addItem(CrossSections.addCrossSection(crossSections[0].size() - 1),
                crossSections[0].size() - 1);
    }

    private void showAddDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddDialogFragment();
        dialog.show(this.getSupportFragmentManager(), "AddDialogFragment");
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
}
