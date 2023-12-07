package com.app.mybase.di.modules

import com.app.mybase.BuildConfig
import com.app.mybase.network.ApiStories
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun getGson(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideOKHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

        okHttpClient.addInterceptor(httpLoggingInterceptor)

        return okHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Singleton
    @Provides
    @Named("news_base_url")
    fun getNewsApi(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.news_base_url).client(okHttpClient).build()
    }

    @Singleton
    @Provides
    @Named("news_base_url")
    fun provideNewsApi(@Named("news_base_url") retrofit: Retrofit): ApiStories {
        return retrofit.create(ApiStories::class.java)
    }

    @Singleton
    @Provides
    @Named("weather_base_url")
    fun getWeatherApi(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.weather_base_url).client(okHttpClient).build()
    }

    @Singleton
    @Provides
    @Named("weather_base_url")
    fun provideWeatherApi(@Named("weather_base_url") retrofit: Retrofit): ApiStories {
        return retrofit.create(ApiStories::class.java)
    }

}