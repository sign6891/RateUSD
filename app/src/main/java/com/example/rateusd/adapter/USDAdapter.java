package com.example.rateusd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rateusd.R;
import com.example.rateusd.model.Record;

import java.util.ArrayList;

public class USDAdapter extends RecyclerView.Adapter<USDAdapter.ViewHolder> {

    private ArrayList<Record> recordArrayList;

    public USDAdapter(ArrayList<Record> recordArrayList) {
        this.recordArrayList = recordArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usd, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.priceTextView.setText(recordArrayList.get(position).getValue());
        holder.dateTextView.setText(recordArrayList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return recordArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView priceTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
