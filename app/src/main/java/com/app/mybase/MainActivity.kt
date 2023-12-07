package com.app.mybase

import android.os.Build
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityMainBinding
import com.app.mybase.helper.openActivity
import com.app.mybase.views.newslist.NewsListActivity
import com.app.mybase.views.weather.WeatherActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    val TAG = this::class.java.name

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreenVisibility()
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this@MainActivity

        clickerListeners()
    }

    private fun clickerListeners() {
        binding.btnPhase1.setOnClickListener {
            openActivity(NewsListActivity::class.java)
        }
        binding.btnPhase2.setOnClickListener {
            openActivity(WeatherActivity::class.java)
        }
    }

    private fun splashScreenVisibility() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            this@MainActivity.setTheme(R.style.Theme_App_SplashScreen)
            Thread.sleep(1000)
            installSplashScreen()
        }
    }

}