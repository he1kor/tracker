package com.helkor.project.global;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HelloWorldService {
    @GET("/")
    Call<String> sendText(@Query("firstname") String text,@Query("lastname") String text2);
}
