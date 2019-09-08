package com.example.bucklingcalculator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChartsAdapter extends RecyclerView.Adapter<ChartsAdapter.ViewHolder> {
    private List<Chart> chartList;
    private Context context;



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView chartTextView;
        private Context context;

        ViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;
            chartTextView = itemView.findViewById(R.id.chartTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Chart chart = chartList.get(position);

                Intent intent = new Intent(context, chart.getActivityClass());
                context.startActivity(intent);
            }
        }
    }

    ChartsAdapter(Context context, List<Chart> chartList) {
        this.chartList = chartList;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public ChartsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(context, inflater.inflate(R.layout.card_view_chart, parent, false));
    }

    @Override
    public void onBindViewHolder(ChartsAdapter.ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.chartTextView;
        Chart chart = chartList.get(position);

        textView.setText(chart.getName());
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }
}