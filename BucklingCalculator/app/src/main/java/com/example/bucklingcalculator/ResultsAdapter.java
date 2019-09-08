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

import static com.example.bucklingcalculator.MainActivity.convert;
import static com.example.bucklingcalculator.MainActivity.results;

public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList list;
    private List<Chart> chartList;
    private Context context;
    private static final int ITEM_TYPE_RESULTS = 0;
    private static final int ITEM_TYPE_CHARTS = 1;

    public class ResultsViewHolder extends RecyclerView.ViewHolder {
        TextView resultsTextView;

        ResultsViewHolder(View itemView) {
            super(itemView);

            resultsTextView = itemView.findViewById(R.id.resultsTextView);
        }
    }

    public class ChartsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView chartTextView;
        private Context context;

        ChartsViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;
            chartTextView = itemView.findViewById(R.id.chartTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Chart chart = chartList.get(position-1);

                Intent intent = new Intent(context, chart.getActivityClass());
                context.startActivity(intent);
            }
        }
    }

    ResultsAdapter(Context context, ArrayList list, List<Chart> chartList) {
        this.chartList = chartList;
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Chart) {
            return ITEM_TYPE_CHARTS;
        } else {
            return ITEM_TYPE_RESULTS;
        }
    }

    private Context getContext() {
        return context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ITEM_TYPE_CHARTS) {
            return new ChartsViewHolder(context, inflater.inflate(R.layout.card_view_chart, parent, false));
        } else {
            return new ResultsViewHolder(inflater.inflate(R.layout.card_view_results, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ChartsViewHolder) {
            TextView chartTextView = ((ChartsViewHolder) viewHolder).chartTextView;
            Chart chart = chartList.get(position-1);
            chartTextView.setText(chart.getName());
        } else {
            TextView resultsTextView = ((ResultsViewHolder) viewHolder).resultsTextView;
            resultsTextView.append("\n\n" + getContext().getString(R.string.critical_stress) + ":" +
                    " " +
                    convert(results.get(0), 2) + getContext().getString(R.string.stress_unit_si));
            resultsTextView.append("\n" + getContext().getString(R.string.critical_force) + ": " +
                    convert(results.get(1), 2) + getContext().getString(R.string.force_unit_si));
            resultsTextView.append("\n" + getContext().getString(R.string.safety_factor) + ": " +
                    convert(results.get(2), 2));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}