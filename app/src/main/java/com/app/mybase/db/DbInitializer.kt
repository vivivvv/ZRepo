package com.app.mybase.db

import android.content.Context
import androidx.room.Room

class DbInitializer(private val context: Context) {

    fun initDatabase(): NewsDatabase {
        return Room
            .databaseBuilder(context, NewsDatabase::class.java, "News_db")
            .build()
    }
}