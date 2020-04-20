package com.example.rateusd.work;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.rateusd.MainActivity;
import com.example.rateusd.R;
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

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class MyWorker extends Worker {

    private ArrayList<Record> recordList = new ArrayList<>();
    private String dateFinish;
    private String dateStart;
    private String getPriceNetwork;
    private static final String TAG = MyWorker.class.getName();
    private static final String CHANNEL_ID = "CHANNEL_ID";


    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        loadRateUSD();

        Data data = getInputData();
        String getPriceThis = data.getString(MainActivity.KEY_TASK_DESC);

        getDataNetwork(getPriceThis, getPriceNetwork);
        return Result.success();
    }

    private void getDataNetwork(String priceThis, String priceNetwork) {
        double currentRate = 0;
        double setRate = 0;

        if (!priceNetwork.isEmpty() && !priceThis.isEmpty()) {
            priceNetwork = priceNetwork.replace(',', '.');
            currentRate = Double.parseDouble(priceNetwork);

            priceThis = priceThis.replace(',', '.');
            setRate = Double.parseDouble(priceThis);
        }
        Log.d("WorkManagerTrue", "Последовательность: " + 6 + " getDataNetwork");
        if (currentRate > setRate) {
            displayNotification();
        }
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
            } else {
                Log.d(TAG, "Retrofit Response: " + response.errorBody().string());
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

    public void displayNotification() {

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.flag)// иконка
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Оповещение")
                        .setContentText("Текущий курс Доллара больше заданного")
                        .setPriority(PRIORITY_HIGH);
        createChanelIfNeeded(notificationManager);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void createChanelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
