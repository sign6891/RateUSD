package com.example.rateusd;

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

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class MyWorkerPriceNetwork extends Worker {

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String KEY_TASK_OUTPUT = "key_task_output";

    public MyWorkerPriceNetwork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Data data = getInputData();
        String getPriceThis = data.getString(MainActivity.KEY_TASK_DESC);
        String getPriceNetwork = data.getString(KEY_TASK_OUTPUT);
        getDataNetwork(getPriceThis, getPriceNetwork);

        return Result.success();
    }

    private void getDataNetwork(String priceThis, String priceNetwork) {
        double currentRate = 0;
        double setRate = 0;

        if (priceNetwork != null && priceThis != null) {
            priceNetwork = priceNetwork.replace(',', '.');
            currentRate = Double.parseDouble(priceNetwork);

            priceThis = priceThis.replace(',', '.');
            setRate = Double.parseDouble(priceThis);
        }
        if (currentRate > setRate) {
            displayNotification();
        }
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
