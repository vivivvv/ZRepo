package com.app.mybase.di.modules

import android.app.Application
import com.app.mybase.db.DbInitializer
import com.app.mybase.db.NewsDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideNewsDao(context: Application): NewsDatabase {
        return DbInitializer(context.applicationContext).initDatabase()
    }

}