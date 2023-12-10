package com.app.mybase.network

import com.app.mybase.model.NewsModel
import com.app.mybase.model.WeatherDataModel
import com.app.mybase.model.WeatherForecastModel
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiStories {

    @GET("v4/articles/")
    suspend fun getNewsList(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
    ): NewsModel

    @GET("data/2.5/weather/")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String = "metric"
    ): WeatherDataModel

    @GET("data/2.5/forecast/")
    suspend fun getForecastWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String = "metric"
    ): WeatherForecastModel

}