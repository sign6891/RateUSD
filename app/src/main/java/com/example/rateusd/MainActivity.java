package com.example.rateusd;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rateusd.adapter.USDAdapter;
import com.example.rateusd.model.Record;
import com.example.rateusd.work.MyWorker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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

    public String setPrice;
    private SharedPreferences sharedPreferences;

    private static final String PRICE_KEY = "price_key";
    private static final String APP_PREFERENCES = "app_preferences";
    public static final String KEY_TASK_DESC = "key_task_desc";
    public static final String TAG = "send_reminder_periodic";

    private GetRateUSD getRateUSD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        priceTrackingTextView = findViewById(R.id.priceTrackingTextView);
        progressBar = findViewById(R.id.progress_bar);
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        loadSetRate();

        recordArrayList = new ArrayList<>();
        getRateUSD = new GetRateUSD(this);

        getRateUSD.showRateUSD();

        Log.d("WorkManagerTrue", "Последовательность: " + 1 + "onCreate");
        startWorker();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditPrice();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRateUSD.showRateUSD();
            }
        });
    }

    private void startWorker() {
        Log.d("WorkManagerTrue", "Последовательность: " + 2 + "startWorker");
        Data data = new Data.Builder()
                .putString(KEY_TASK_DESC, priceTrackingTextView.getText().toString())
                .build();

        //Запускать процесс только при наличие Интернета
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        //Запускает процесс в указанный интервал(каждые (минимум)15 мин один раз в течении заданного промежутка)
        PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(
                MyWorker.class,
                24, TimeUnit.HOURS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.MINUTES)
                .setInputData(data)
                .addTag("send_reminder_periodic")
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, myWorkRequest);

        WorkManager.getInstance().getWorkInfoByIdLiveData(myWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        String status = workInfo.getState().name();
                        Log.d("WorkManagerTrue", "onChanged " + Thread.currentThread().getName() + " " + status);
                    }
                });
    }


    private void addAndEditPrice() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.add_price, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView newPriceTitle = view.findViewById(R.id.newPriceTitle);
        final EditText newPriceEditText = view.findViewById(R.id.newPriceEditText);

        //Метод для появления нужной клавиатуры с ","
        getRateUSD.localeDecimalInput(newPriceEditText);

        if (!TextUtils.isEmpty(priceTrackingTextView.getText())) {
            newPriceEditText.setText(priceTrackingTextView.getText());
            isUpdate = true;
        }

        newPriceTitle.setText(!isUpdate ? "Add Price" : "Edit Price");

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
                            setPrice = "";
                            saveSetRate();
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

                    Toast.makeText(MainActivity.this, "Enter price!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                if (!TextUtils.isEmpty(newPriceEditText.getText())) {
                    priceTrackingTextView.setText(newPriceEditText.getText());
                    setPrice = priceTrackingTextView.getText().toString();
                    saveSetRate();
                    isUpdate = true;
                } else {
                    isUpdate = false;
                }
            }
        });
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

    @SuppressLint("ApplySharedPref")
    private void saveSetRate() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PRICE_KEY, setPrice);
        editor.commit();
    }

    private void loadSetRate() {
        setPrice = sharedPreferences.getString(PRICE_KEY, null);
        if (setPrice != null) {
            priceTrackingTextView.setText(setPrice);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSetRate();
    }
}
