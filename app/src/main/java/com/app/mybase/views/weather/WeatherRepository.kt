package com.app.mybase.views.weather

import com.app.mybase.db.NewsDatabase
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants
import com.app.mybase.model.WeatherDataModel
import com.app.mybase.model.WeatherForecastModel
import com.app.mybase.network.ApiStories
import javax.inject.Inject
import javax.inject.Named

class WeatherRepository @Inject constructor(
    @Named("weather_base_url") var apiStories: ApiStories,
    val db: NewsDatabase
) {

    suspend fun getWeather(
        lat: String,
        lon: String,
        appid: String
    ): ApisResponse<WeatherDataModel> {
        return try {
            val callApi = apiStories.getWeather(lat, lon, appid)
            ApisResponse.Success(callApi)
        } catch (e: Exception) {
            ApisResponse.Error(e.message ?: AppConstants.SOMETHING_WENT_WRONG)
        }
    }

    suspend fun getForecastWeather(
        lat: String,
        lon: String,
        appid: String
    ): ApisResponse<WeatherForecastModel> {
        return try {
            val callApi = apiStories.getForecastWeather(lat, lon, appid)
            ApisResponse.Success(callApi)
        } catch (e: Exception) {
            ApisResponse.Error(e.message ?: AppConstants.SOMETHING_WENT_WRONG)
        }
    }

}