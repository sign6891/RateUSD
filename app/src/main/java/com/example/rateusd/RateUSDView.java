package com.example.rateusd;

import com.example.rateusd.model.Record;

import java.util.ArrayList;

public interface RateUSDView {
    void fillRecyclerView(ArrayList<Record> recordArrayList);
    void showNetworkError();
}
