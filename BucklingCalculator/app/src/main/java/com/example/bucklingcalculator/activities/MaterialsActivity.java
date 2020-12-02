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
import com.example.bucklingcalculator.adapters.MaterialsAdapter;
import com.example.bucklingcalculator.models.Materials;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tobiasschuerg.prefixsuffix.PrefixSuffixEditText;

import static com.example.bucklingcalculator.activities.MainActivity.materials;

public class MaterialsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static MaterialsAdapter materialsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materials);
        setTitle(R.string.materials_option);
        setupSharedPreferences();

        RecyclerView recyclerView = findViewById(R.id.list);
        materialsAdapter = new MaterialsAdapter(Materials.ITEMS, getSupportFragmentManager());

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(materialsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddDialog());
    }

    public static class AddDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_add_material, container, false);

            EditText editText1 = view.findViewById(R.id.dialogEditText1);
            PrefixSuffixEditText editText2 = view.findViewById(R.id.dialogEditText2);
            editText2.setSuffix(" Pa");
            PrefixSuffixEditText editText3 = view.findViewById(R.id.dialogEditText3);
            editText3.setSuffix(" Pa");

            Button saveButton = view.findViewById(R.id.dialogSaveButton);
            saveButton.setOnClickListener(v -> {
                materials[0].add(editText1.getText().toString());
                materials[1].add(editText2.getText().toString());
                materials[2].add(editText3.getText().toString());
                materialsAdapter.addItem(Materials.addMaterial(materials[0].size()-1),
                        materials[0].size()-1);
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
