package com.app.mybase.views.newslist

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.R
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityNewsListBinding
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants.PAGE_URL
import com.app.mybase.helper.openActivity
import com.app.mybase.model.Result
import com.app.mybase.views.newslist.adapters.LoadMoreAdapter
import com.app.mybase.views.newslist.adapters.NewsAdapter
import com.app.mybase.views.webview.WebViewActivity
import dagger.android.AndroidInjection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsListActivity : BaseActivity<ActivityNewsListBinding, NewsListViewModel>() {

    val TAG = this::class.java.name

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var newsAdapter: NewsAdapter
    lateinit var newsRecyclerView: RecyclerView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_list)
        viewModel = ViewModelProvider(this, factory)[NewsListViewModel::class.java]
        binding.newsListViewModel = viewModel
        binding.lifecycleOwner = this@NewsListActivity

        initializeBindingData()
        initializeNewsRV()
        // Getting News List
        getNewsList()
        // Set search listener
        searchListener()

        // For footer progress
        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collect {
                val state = it.refresh
                viewModel.showLoading.value = state is LoadState.Loading
            }
        }

        // For load footer retry
        newsRecyclerView.adapter = newsAdapter.withLoadStateFooter(
            LoadMoreAdapter {
                newsAdapter.retry()
            }
        )

        newsAdapter.setOnItemClickListener { result ->
            openActivity(WebViewActivity::class.java, extras = { putString(PAGE_URL, result.url) })
        }

    }

    private fun searchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                lifecycleScope.launch {
                    viewModel.search(newText ?: "").collectLatest {
                        // Clear the previous data before submitting new data
                        newsAdapter.submitData(PagingData.empty())
                        newsAdapter.submitData(it)
                    }
                }
                return true
            }
        })
    }

    private fun getNewsList() {
        viewModel.getNewsList().observe(this, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    if (apiResponse.response.results.isNotEmpty()) {
                        updateUserListToUIAndDatabase(apiResponse.response.results)
                    }
                }
                is ApisResponse.Error -> {
                    showToast(apiResponse.exception, Toast.LENGTH_SHORT)
                }
                is ApisResponse.Loading -> {
                    viewModel.showProgress()
                }
                is ApisResponse.Complete -> {
                    viewModel.hideProgress()
                }
                else -> {}
            }
        })
    }

    private fun updateUserListToUIAndDatabase(results: List<Result>) {
        val newsList = ArrayList<Result>()
        results.forEach {
            newsList.add(
                Result(
                    it.id,
                    it.featured,
                    it.image_url,
                    it.news_site,
                    it.published_at,
                    it.summary,
                    it.title,
                    it.updated_at,
                    it.url
                )
            )
        }
        lifecycleScope.launch {
            // Inserting all the data to the Local database
            viewModel.insertAll(newsList)
            //  Collect live data from db and show UI
            viewModel.newsList.collectLatest {
                newsAdapter.submitData(it)
            }
        }
    }

    private fun initializeBindingData() {
        newsRecyclerView = binding.newsRv
        searchView = binding.searchView
    }

    private fun initializeNewsRV() {
        newsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        newsRecyclerView.adapter = newsAdapter
    }

}