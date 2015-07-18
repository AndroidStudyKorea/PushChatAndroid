package com.androidstudy.pushchat.network;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import com.androidstudy.pushchat.TalkModel;

public interface ApiService
{
    @POST("/talk/")
    void talk_create(@Body TalkModel menu, Callback<TalkModel> cb);
}