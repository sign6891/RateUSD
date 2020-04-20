package com.example.rateusd.loadnetworkrate;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.rateusd.model.Record;
import com.example.rateusd.model.ValCurs;
import com.example.rateusd.service.RetrofitInstance;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetRateUSD {

    private static final String TAG = GetRateUSD.class.getName();
    @Nullable
    private RateUSDView rateUSDView;
    private ArrayList<Record> recordArrayList = new ArrayList<>();
    private String dateFinish;
    private String dateStart;

    public GetRateUSD(RateUSDView rateUSDView) {
        this.rateUSDView = rateUSDView;
    }

    public Object showRateUSD() {

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
                                    rateUSDView.fillRecyclerView(recordArrayList);
                                }
                            } else {
                                Log.d(TAG, "Retrofit Response: " + response.errorBody().string());
                                Log.d(TAG, "Error message: " + response.raw().message());
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "Exception: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ValCurs> call, Throwable t) {
                        rateUSDView.showNetworkError();
                        Log.d(TAG, "Exception: " + t.getMessage());
                    }
                });
        return recordArrayList;
    }

    private void dateFormat() {
        //Получение даты
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        dateFinish = dateFormat.format(calendar.getTime());

        //Получение даты за вычетом одного месяца
        calendar.add(Calendar.MONTH, -1);
        dateStart = dateFormat.format(calendar.getTime());
    }

    //Метод для редактирования клавиатуры для ввода суммы(",")
    public void localeDecimalInput(final EditText editText){
        DecimalFormat decFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols=decFormat.getDecimalFormatSymbols();
        final String defaultSeperator=Character.toString(symbols.getDecimalSeparator());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().contains(defaultSeperator))
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789" + defaultSeperator));
            }
        });
    }

}
