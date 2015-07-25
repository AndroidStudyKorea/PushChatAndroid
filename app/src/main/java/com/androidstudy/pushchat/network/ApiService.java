package com.androidstudy.pushchat.network;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface ApiService
{
    @POST("/device/")
    void device_create(@Body DeviceModel talk, Callback<DeviceModel> cb);

    @GET("/device/")
    void device_list(Callback<DeviceListResponse> cb);

    @POST("/talk/")
    void talk_create(@Body TalkModel talk, Callback<TalkModel> cb);
}