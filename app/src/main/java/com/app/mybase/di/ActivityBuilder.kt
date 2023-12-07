package com.app.mybase.di

import com.app.mybase.MainActivity
import com.app.mybase.di.modules.MainModule
import com.app.mybase.di.modules.NewsListModule
import com.app.mybase.di.modules.WeatherModule
import com.app.mybase.di.modules.WebViewModule
import com.app.mybase.views.newslist.NewsListActivity
import com.app.mybase.views.weather.WeatherActivity
import com.app.mybase.views.webview.WebViewActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [NewsListModule::class])
    abstract fun provideNewsListActivity(): NewsListActivity

    @ContributesAndroidInjector(modules = [WebViewModule::class])
    abstract fun provideWebViewActivity(): WebViewActivity

    @ContributesAndroidInjector(modules = [WeatherModule::class])
    abstract fun provideWeatherActivity(): WeatherActivity


}