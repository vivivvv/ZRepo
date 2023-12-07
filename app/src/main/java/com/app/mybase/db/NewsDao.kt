package com.app.mybase.db


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.mybase.model.Result

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userEntity: List<Result>)

    @Query("SELECT * FROM Result ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun getAllUser(limit: Int, offset: Int): List<Result>

    @Query("SELECT * FROM Result ORDER BY id DESC LIMIT :limit OFFSET :offset")
    suspend fun getNewsList(limit: Int, offset: Int): List<Result>

    @Query("SELECT * FROM Result WHERE title LIKE :query ORDER BY id DESC")
    fun search(query: String): PagingSource<Int, Result>


}