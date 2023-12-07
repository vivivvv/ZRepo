package com.app.mybase.views.weather

import androidx.lifecycle.liveData
import com.app.mybase.base.BaseViewModel
import com.app.mybase.helper.ApisResponse
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {


    fun getWeather(lat: String, lon: String, appid: String) = liveData(Dispatchers.IO) {
        emit(ApisResponse.Loading)
        emit(weatherRepository.getWeather(lat, lon, appid))
        emit(ApisResponse.Complete)
    }

    fun getForecastWeather(lat: String, lon: String, appid: String) = liveData(Dispatchers.IO) {
        emit(ApisResponse.Loading)
        emit(weatherRepository.getForecastWeather(lat, lon, appid))
        emit(ApisResponse.Complete)
    }

}