package com.app.mybase.di.modules

import androidx.lifecycle.ViewModelProvider
import com.app.mybase.helper.ViewModelProviderFactory
import com.app.mybase.views.weather.WeatherViewModel
import dagger.Module
import dagger.Provides

@Module
class WeatherModule {

    @Provides
    fun provideViewModelProvider(viewModel: WeatherViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}