package com.example.bucklingcalculator.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bucklingcalculator.R;
import com.example.bucklingcalculator.models.Materials;

import java.util.List;

import static com.example.bucklingcalculator.activities.MainActivity.materials;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.ViewHolder> {
    FragmentManager fragmentManager;

    private static List<Materials.Material> mValues;

    public MaterialsAdapter(List<Materials.Material> items, FragmentManager fragmentManager) {
        mValues = items;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).content);
        holder.mDetails.setText(mValues.get(position).details);

        holder.mView.setOnClickListener(v -> showEditDialog(position));

        holder.mDelete.setOnClickListener(v -> {
            deleteItem(position);
            for(int i = 0; i < materials.length; i++) {
                materials[i].remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mDetails;
        public final ImageView mDelete;
        public Materials.Material mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mDetails = view.findViewById(R.id.details);
            mDelete = view.findViewById(R.id.delete);
        }
    }

    public void addItem(Materials.Material item, int index) {
        mValues.add(index, item);
        notifyDataSetChanged();
    }

    private void editItem(Materials.Material item, int index) {
        mValues.set(index, item);
        notifyDataSetChanged();
    }

    public void deleteItem(int index) {
        mValues.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mValues.size());
    }

    public static class EditDialogFragment extends DialogFragment {
        private int position;
        MaterialsAdapter materialsAdapter;

        private EditDialogFragment(MaterialsAdapter materialsAdapter, int position) {
            super();
            this.materialsAdapter = materialsAdapter;
            this.position = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_edit_material, container, false);
            EditText editText1 = view.findViewById(R.id.dialogEditText1);
            editText1.setText(materials[0].get(position));
            EditText editText2 = view.findViewById(R.id.dialogEditText2);
            editText2.setText(materials[1].get(position));
            EditText editText3 = view.findViewById(R.id.dialogEditText3);
            editText3.setText(materials[2].get(position));
            Button saveButton = view.findViewById(R.id.dialogSaveButton);
            saveButton.setOnClickListener(v -> {
                materials[0].set(position, editText1.getText().toString());
                materials[1].set(position, editText2.getText().toString());
                materials[2].set(position, editText3.getText().toString());
                materialsAdapter.editItem(Materials.addMaterial(
                        position),
                        position);
                EditDialogFragment.this.dismiss();
            });
            Button cancelButton = view.findViewById(R.id.dialogCancelButton);
            cancelButton.setOnClickListener(v -> EditDialogFragment.this.getDialog().cancel());

            return view;
        }
    }

    public void showEditDialog(int position) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new EditDialogFragment(this, position);
        dialog.show(fragmentManager, "EditDialogFragment");
    }
}
