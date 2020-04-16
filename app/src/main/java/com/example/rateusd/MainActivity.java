package com.example.rateusd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.rateusd.adapter.USDAdapter;
import com.example.rateusd.model.Record;
import com.example.rateusd.model.ValCurs;
import com.example.rateusd.service.RetrofitInstance;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Record> recordArrayList;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private USDAdapter adapter;
    private ProgressBar progressBar;

    private String dateFinish;
    private String dateStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progress_bar);
        recordArrayList = new ArrayList<>();

        showRateUSD();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRateUSD();
            }
        });
    }

    private Object showRateUSD() {

        dateFormat();

        RetrofitInstance.getInstance()
                 .getValCurs(dateStart, dateFinish, "R01235")
                .enqueue(new Callback<ValCurs>() {
                    @Override
                    public void onResponse(Call<ValCurs> call, Response<ValCurs> response) {
                        try {
                            if (response.isSuccessful()) {

                                ValCurs valCurs = response.body();
                                if (valCurs != null) {
                                    recordArrayList = (ArrayList<Record>) valCurs.getRecords();
                                    fillRecyclerView();
                                }
                            } else {
                                Log.d("TAG", "Retrofit Response: " + response.errorBody().string());
                                Log.d("TAG", "Error message: " + response.raw().message());
                                Log.d("TAG", "Error code: " + String.valueOf(response.raw().code()));
                            }
                        } catch (IOException e) {
                            Log.d("LOG_ERROR", "Exception: " + e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ValCurs> call, Throwable t) {
                        showNetworkError();
                        Log.d("LOG_ERROR", "Exception: " + t.getMessage());
                    }
                });
        return recordArrayList;
    }

    private void dateFormat(){
        //Получение даты
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        dateFinish = dateFormat.format(calendar.getTime());

        //Получение даты за вычетом одного месяца
        calendar.add(Calendar.MONTH, -1);
        dateStart = dateFormat.format(calendar.getTime());
    }

    public void showNetworkError() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        snackbar = Snackbar.make(swipeRefreshLayout, "Connection problems, only cached data is shown." +
                " Check the connection or try again later.", Snackbar.LENGTH_LONG)
                .setAction("OK", (d) -> snackbar.dismiss());
        snackbar.show();
    }

    private void fillRecyclerView() {

        recyclerView = findViewById(R.id.recycler_view);
        Collections.reverse(recordArrayList);
        adapter = new USDAdapter(recordArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

    }
}
