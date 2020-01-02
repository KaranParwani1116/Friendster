package com.example.friendster.rest;

import android.app.Activity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient extends Activity {
    public static final String Base_Url="http://10.0.2.2/friendster/public/app/";
    public static final String Base_Url_1="http://10.0.2.2/friendster/public/";

    private static Retrofit retrofit=null;

    public static Retrofit getApiClient()
    {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build();
        if(retrofit==null)
        {
            retrofit=new Retrofit.Builder().baseUrl(Base_Url)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
