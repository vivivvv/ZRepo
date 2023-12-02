package com.app.mybase

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityMainBinding
import com.app.mybase.helper.ApisResponse
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : BaseActivity() {

    val TAG = this::class.java.name
    lateinit var binding: ActivityMainBinding
    lateinit var viewmodel: MainViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewmodel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        binding.mainViewModel = viewmodel
        binding.lifecycleOwner = this@MainActivity

        binding.textView.text = viewmodel.getName()
        performApi()
    }

    fun performApi() {
        viewmodel.getNotificationCount().observe(this, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    Log.d(TAG, "getNotifications count: $apiResponse")
                }
                is ApisResponse.Error -> {
                    Log.d(TAG, "getNotifications count: error")
                }
                is ApisResponse.Loading -> {
                    viewmodel.showProgress()
                }
                is ApisResponse.Complete -> {
                    viewmodel.hideProgress()
                }
                else -> {}
            }
        })
    }

}