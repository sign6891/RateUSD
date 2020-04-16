package com.example.rateusd.service;

import com.example.rateusd.model.ValCurs;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface USDEndpoint {
    //http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=02/03/2001&date_req2=14/03/2001&VAL_NM_RQ=R01235

   // @GET("XML_dynamic.asp?date_req1=02/03/2001&date_req2=14/03/2001&VAL_NM_RQ=R01235")
    @GET("XML_dynamic.asp")
    Call<ValCurs> getValCurs(@Query("date_req1") String date1,
                             @Query("date_req2") String date2,
                             @Query("VAL_NM_RQ") String id);

}
