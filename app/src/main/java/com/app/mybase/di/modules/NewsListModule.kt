package com.app.mybase.di.modules

import androidx.lifecycle.ViewModelProvider
import com.app.mybase.helper.ViewModelProviderFactory
import com.app.mybase.views.newslist.NewsListViewModel
import dagger.Module
import dagger.Provides

@Module
class NewsListModule {

    @Provides
    fun provideViewModelProvider(viewModel: NewsListViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }

}