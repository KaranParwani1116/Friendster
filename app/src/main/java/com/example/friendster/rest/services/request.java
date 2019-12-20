package com.example.friendster.rest.services;

import com.example.friendster.activity.Loginactivity;
import com.example.friendster.model.User;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface request {

    @POST("login")
    Call<Integer>signin(@Body Loginactivity.Userinfo userinfo);

    @GET("loadownprofile")
    Call<User>loadownProfile(@QueryMap Map<String,String> params);

    @POST("poststatus")
    Call<Integer> uploadstatus(@Body MultipartBody requestbody);

    @POST("uploadImage")
    Call<Integer> uploadImage(@Body MultipartBody requestbody);


}