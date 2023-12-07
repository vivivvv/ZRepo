package com.app.mybase

import com.app.mybase.network.ApiStories
import javax.inject.Inject
import javax.inject.Named

class MainRepository @Inject constructor(@Named("news_base_url") var apiStories: ApiStories) {

}