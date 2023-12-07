package com.app.mybase.helper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.app.mybase.R


fun <T> Activity.openActivity(
    it: Class<T>,
    finish: Boolean = false,
    extras: (Bundle.() -> Unit)? = null
) {
    val intent = Intent(this, it)

    if (extras != null) {
        intent.putExtras(Bundle().apply(extras))
    }

    startActivity(intent)
    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)

    if (finish) finish()
}