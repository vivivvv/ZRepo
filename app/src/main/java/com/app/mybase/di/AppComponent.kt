package com.app.mybase.di

import com.app.mybase.MyApp
import com.app.mybase.di.modules.ApiModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class, ApiModule::class, ActivityBuilder::class]
)
interface AppComponent {
    /*  This is our custom Application class*/
    fun inject(application: MyApp)
}