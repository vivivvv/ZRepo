package com.app.mybase.di

import com.app.mybase.MainActivity
import com.app.mybase.di.modules.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun provideMainActivity(): MainActivity


}