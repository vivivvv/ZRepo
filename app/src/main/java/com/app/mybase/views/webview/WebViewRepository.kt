package com.app.mybase.views.webview

import com.app.mybase.db.NewsDatabase
import com.app.mybase.network.ApiStories
import javax.inject.Inject
import javax.inject.Named

class WebViewRepository  @Inject constructor(@Named("news_base_url") var apiStories: ApiStories, val db: NewsDatabase) {
}