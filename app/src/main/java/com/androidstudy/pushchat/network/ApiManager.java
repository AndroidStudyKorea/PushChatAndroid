package com.androidstudy.pushchat.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class ApiManager
{
    public static final String API_URL   = "http://192.168.1.42:8000/api";

	private static Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
			.create();

	public static RestAdapter restAdapter = new RestAdapter.Builder()
			.setEndpoint(API_URL)
			.setLogLevel(RestAdapter.LogLevel.BASIC)
			.setConverter(new GsonConverter(gson))
			.build();

	public static ApiService service = restAdapter.create(ApiService.class);
}