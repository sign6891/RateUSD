package com.example.rateusd.service;

import java.util.SimpleTimeZone;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit = null;
    private final static String BASE_URL = "http://www.cbr.ru/scripts/";

    public static USDEndpoint getInstance() {
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        }
        return retrofit.create(USDEndpoint.class);
    }
}
