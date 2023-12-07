package com.app.mybase.di.modules

import androidx.lifecycle.ViewModelProvider
import com.app.mybase.helper.ViewModelProviderFactory
import com.app.mybase.views.webview.WebViewViewModel
import dagger.Module
import dagger.Provides

@Module
class WebViewModule {

    @Provides
    fun provideViewModelProvider(viewModel: WebViewViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }

}