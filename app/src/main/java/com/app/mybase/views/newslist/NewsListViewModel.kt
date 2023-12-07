package com.app.mybase.views.newslist

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.mybase.base.BaseViewModel
import com.app.mybase.helper.ApisResponse
import com.app.mybase.model.Result
import com.app.mybase.views.newslist.paging.NewsPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsListViewModel @Inject constructor(
    private val newsListRepository: NewsListRepository
) : BaseViewModel() {

    val newsList = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 10
        ),
    ) {
        NewsPagingSource(newsListRepository)
    }.flow.cachedIn(viewModelScope)

    fun getNewsList() = liveData(Dispatchers.IO) {
        emit(ApisResponse.Loading)
        emit(newsListRepository.getNewsList())
        emit(ApisResponse.Complete)
    }

    suspend fun insertAll(newsEntityList: List<Result>) {
        withContext(Dispatchers.IO) {
            newsListRepository.insertAll(newsEntityList)
        }
    }

    private var currentQuery: String? = null

    fun search(query: String): Flow<PagingData<Result>> {
        currentQuery = query
        return newsListRepository.search(query)
            .flow
            .cachedIn(viewModelScope)
    }

}
