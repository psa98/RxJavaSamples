package com.example.rxjavasamples.retrofit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient INSTANCE;
    private final Retrofit retrofit;

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private RetrofitClient(){
        String BASE_URL = "https://jsonplaceholder.typicode.com/";
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                /* Отличия при использовании Рх с Ретрофит
                 * добавляем .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                 */
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }
    public static synchronized RetrofitClient getInstance(){
        if(INSTANCE == null){
            INSTANCE = new RetrofitClient();
        }
        return INSTANCE;
    }
    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
