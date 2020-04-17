package com.example.rateusd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rateusd.adapter.USDAdapter;
import com.example.rateusd.model.Record;
import com.example.rateusd.model.ValCurs;
import com.example.rateusd.service.RetrofitInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RateUSDView {

    private ArrayList<Record> recordArrayList;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private USDAdapter adapter;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;

    private TextView priceTrackingTextView;
    private boolean isUpdate = false;

    private String setPrice;

    private GetRateUSD getRateUSD;
    //private ShowAlertDialog showAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        priceTrackingTextView = findViewById(R.id.priceTrackingTextView);
        progressBar = findViewById(R.id.progress_bar);

       // showAlertDialog = new ShowAlertDialog(this);

        getRateUSD = new GetRateUSD(this);

        PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,
                15, TimeUnit.MINUTES, 20, TimeUnit.MINUTES).build();

        recordArrayList = new ArrayList<>();

        getRateUSD.showRateUSD();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrice = addAndEditPrice();
               // setPrice = showAlertDialog.addAndEditPrice(priceTrackingTextView.getText().toString());
                //priceTrackingTextView.setText(setPrice);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRateUSD.showRateUSD();
            }
        });

        //Делать запрос в сеть раз в сутки в бекграунде
        WorkManager.getInstance().getWorkInfoByIdLiveData(myWorkRequest.getId()).observe(this,
                new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        String status = workInfo.getState().name();

                    }
                });
    }


    private String addAndEditPrice() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.add_price, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView newPriceTitle = view.findViewById(R.id.newPriceTitle);
        final EditText newPriceEditText = view.findViewById(R.id.newPriceEditText);

        //Метод для появления нужной клавиатуры с ","
        getRateUSD.localeDecimalInput(newPriceEditText);

        newPriceTitle.setText(!isUpdate ? "Add Price" : "Edit Price");

        if (isUpdate && !TextUtils.isEmpty(priceTrackingTextView.getText())) {
            newPriceEditText.setText(priceTrackingTextView.getText());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).setNegativeButton(isUpdate ? "Delete" : "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isUpdate) {
                            priceTrackingTextView.setText("");
                            isUpdate = false;
                        } else {
                            dialog.cancel();
                        }
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(newPriceEditText.getText().toString())) {

                    Toast.makeText(MainActivity.this, "Enter car name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                if (!TextUtils.isEmpty(newPriceEditText.getText())) {
                    priceTrackingTextView.setText(newPriceEditText.getText());
                    isUpdate = true;
                } else {
                    isUpdate = false;
                }
            }
        });
        return priceTrackingTextView.getText().toString();
    }

    public void showNetworkError() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        snackbar = Snackbar.make(swipeRefreshLayout, "Connection problems, only cached data is shown." +
                " Check the connection or try again later.", Snackbar.LENGTH_LONG)
                .setAction("OK", (d) -> snackbar.dismiss());
        snackbar.show();
    }

    public void fillRecyclerView(ArrayList<Record> recordArrayList) {

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
