package com.flybbird.hellorxandroid.data;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by SuyoungKang on 2016. 2. 19..
 */
public interface WeatherApi {

    //api.openweathermap.org/data/2.5/forecast/city?id=524901&APPID=1111111111
    // penweathermap.org/data/2.5/weather?q=Seoul
    //?APPID="+OPEN_API_REG_KEY +
    @GET("/data/2.5/{name}")
    public Observable<WeatherEntity> get(@Path("name") String name, @Query("q") String q, @Query("APPID") String authKey);


//    @GET("/data/2.5/{name}")
//    public Observable<WeatherEntity> get(@Path("name") String name, @Query("q") String q);
}
