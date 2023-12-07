package com.app.mybase.views.newslist

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.app.mybase.db.NewsDao
import com.app.mybase.db.NewsDatabase
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants
import com.app.mybase.model.NewsModel
import com.app.mybase.model.Result
import com.app.mybase.network.ApiStories
import javax.inject.Inject
import javax.inject.Named

class NewsListRepository @Inject constructor(@Named("news_base_url") var apiStories: ApiStories, val db: NewsDatabase) {

    private val newsDao: NewsDao by lazy { db.newsDao() }

    suspend fun getNewsLocalList(limit: Int, offset: Int) = newsDao.getNewsList(limit, offset)

    suspend fun getNewsList(): ApisResponse<NewsModel> {
        return try {
            val callApi = apiStories.getNewsList()
            ApisResponse.Success(callApi)
        } catch (e: Exception) {
            ApisResponse.Error(e.message ?: AppConstants.SOMETHING_WENT_WRONG)
        }
    }

    suspend fun insertAll(userEntityList: List<Result>) {
        newsDao.insertAll(userEntityList)
    }

    fun search(query: String): Pager<Int, Result> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { newsDao.search("%$query%") }
        )
    }

}