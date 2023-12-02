package com.app.mybase.network

import com.app.mybase.model.UserDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiStories {

    @GET("no-auth/api/authentication/user-info")
    suspend fun getNotificationCount(@Query("loginName") loginName: String): UserDetailsResponse

//    @GET("getAllUsers/{topic}")
//    suspend fun getAllUsers(@Path("topic") String topic): List<User>
}