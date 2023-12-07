package com.app.mybase.views.splash

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.mybase.MainActivity
import com.app.mybase.R
import com.app.mybase.helper.openActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            openActivity(MainActivity::class.java, true)
        } else {
            setContentView(R.layout.activity_splash)
            lifecycleScope.launch {
                delay(1000)
                openActivity(MainActivity::class.java, true)
            }
        }
    }
}