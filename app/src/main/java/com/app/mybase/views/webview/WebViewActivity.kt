package com.app.mybase.views.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.mybase.R
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityWebViewBinding
import com.app.mybase.helper.AppConstants.PAGE_URL
import dagger.android.AndroidInjection
import javax.inject.Inject

class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewViewModel>() {

    val TAG = this::class.java.name

    private var webClient: NewsWebViewClient? = null
    private lateinit var pageUrl: String

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        viewModel = ViewModelProvider(this, factory)[WebViewViewModel::class.java]
        binding.webViewViewModel = viewModel
        binding.lifecycleOwner = this@WebViewActivity

        // get pageUrl from String
        pageUrl = intent.getStringExtra(PAGE_URL)
            ?: throw IllegalStateException("field $PAGE_URL missing in Intent")

        initWebClient()
        initWebViewOnKeyListener()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.loadUrl(pageUrl)

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebClient() {
        webClient = NewsWebViewClient { progressBarState ->
            if (progressBarState) viewModel.showProgress() else viewModel.hideProgress()
        }
        webClient?.apply { binding.webView.webViewClient = this }
    }

    private fun initWebViewOnKeyListener() {
        binding.webView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action == MotionEvent.ACTION_UP
                && binding.webView.canGoBack()
            ) {
                binding.webView.goBack()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

}