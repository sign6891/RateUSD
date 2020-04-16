package com.example.rateusd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.rateusd.model.Record;
import com.example.rateusd.model.ValCurs;
import com.example.rateusd.model.Valute;
import com.example.rateusd.service.RetrofitInstance;
import com.example.rateusd.service.USDEndpoint;
import com.google.android.material.snackbar.Snackbar;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Record> recordArrayList;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recordArrayList = new ArrayList<>();

        showRateUSD();
    }

    private void showRateUSD() {

        RetrofitInstance.getInstance()
                 //.getValCurs("15/03/2020", "15/04/2020", "R01235")
                .getValCurs()
                .enqueue(new Callback<ValCurs>() {
                    @Override
                    public void onResponse(Call<ValCurs> call, Response<ValCurs> response) {
                        try {
                            if (response.isSuccessful()) {

                                ValCurs valCurs = response.body();
                                if (valCurs != null) {
                                    recordArrayList = (ArrayList<Record>) valCurs.getRecords();
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
                        showError();
                        Log.d("LOG_ERROR", "Exception: " + t.getMessage());
                    }
                });
    }

    public void showError() {
        snackbar = Snackbar.make(swipeRefreshLayout, "Connection problems, only cached data is shown." +
                " Check the connection or try again later.", Snackbar.LENGTH_LONG)
                .setAction("OK", (d) -> snackbar.dismiss());
        snackbar.show();
    }
}