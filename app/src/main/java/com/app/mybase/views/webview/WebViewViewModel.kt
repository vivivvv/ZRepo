package com.app.mybase.views.webview

import com.app.mybase.base.BaseViewModel
import javax.inject.Inject

class WebViewViewModel @Inject constructor(
    private val webViewRepository: WebViewRepository
) : BaseViewModel() {
}