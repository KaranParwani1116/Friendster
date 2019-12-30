package com.example.friendster.rest.services;

import com.example.friendster.activity.Loginactivity;
import com.example.friendster.activity.ProfileActivity;
import com.example.friendster.adapter.NewsFeedAdapter;
import com.example.friendster.model.FriendModel;
import com.example.friendster.model.PostModel;
import com.example.friendster.model.User;

import java.util.List;
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

    @GET("search")
    Call<List<User>>search(@QueryMap Map<String,String> params);

    @GET("otherprofile")
    Call<User>otherprofile(@QueryMap Map<String,String> params);

    @POST("performAction")
    Call<Integer>performAction(@Body ProfileActivity.PerformAction performAction);

    @GET("loadfriends")
    Call<FriendModel>loadfriends(@QueryMap Map<String, String>params);

    @GET("profiletimeline")
    Call<List<PostModel>>profileTimeline(@QueryMap Map<String, String>params);

    @GET("gettimelinepost")
    Call<List<PostModel>>gettimeline(@QueryMap Map<String, String>params);

    @POST("likeunlike")
    Call<Integer> likeunlike(@Body NewsFeedAdapter.Addlike addlike);
}
