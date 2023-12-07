package com.app.mybase.views.newslist.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.mybase.model.Result
import com.app.mybase.views.newslist.NewsListRepository

class NewsPagingSource(private val repository: NewsListRepository) :
    PagingSource<Int, Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {

        val page = params.key ?: 0

        return try {
            Log.d(
                "TAG",
                "load finally: limit : ${params.loadSize} offset: ${page * params.loadSize}"
            )
            val entities = repository.getNewsLocalList(params.loadSize, page * params.loadSize)
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}