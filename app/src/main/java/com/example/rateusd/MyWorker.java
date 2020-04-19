package com.example.rateusd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.rateusd.model.Record;
import com.example.rateusd.model.ValCurs;
import com.example.rateusd.service.RetrofitInstance;
import com.example.rateusd.service.USDEndpoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

public class MyWorker extends Worker {

    private ArrayList<Record> recordList = new ArrayList<>();
    private String dateFinish;
    private String dateStart;
    private String getPriceNetwork;
    private static final String KEY_TASK_OUTPUT = "key_task_output";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        loadRateUSD();

        Data output = new Data.Builder()
                .putString(KEY_TASK_OUTPUT, getPriceNetwork)
                .build();
        return Result.success(output);
    }


    public void loadRateUSD() {
        dateFormat();

        USDEndpoint usdEndpoint = RetrofitInstance.getInstance();
        Call<ValCurs> call = usdEndpoint.getValCurs(dateStart, dateFinish, "R01235");

        try {
            Response<ValCurs> response = call.execute();
            ValCurs valCurs = response.body();
            if (valCurs != null) {
                recordList = (ArrayList<Record>) valCurs.getRecords();
                Collections.reverse(recordList);
                recordList.get(0).getValue();
                getPriceNetwork = recordList.get(0).getValue();
                Log.d("MyWork", "doWork = " + 5);
                Log.d("MyWork", "getPriceNetwork = " + "  " + getPriceNetwork);
            } else {
                Log.d("TAG", "Retrofit Response: " + response.errorBody().string());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dateFormat() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        dateFinish = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        dateStart = dateFormat.format(calendar.getTime());
    }
}
