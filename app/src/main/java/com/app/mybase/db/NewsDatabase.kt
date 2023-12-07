package com.app.mybase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.mybase.model.Result

@Database(entities = [Result::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

}