package com.app.mybase

import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.AppConstants
import com.app.mybase.model.UserDetailsResponse
import com.app.mybase.network.ApiStories
import javax.inject.Inject

class MainRepository @Inject constructor(var apiStories: ApiStories) {


    fun getName(): String {
        return "vicky"
    }

    suspend fun getNotificationCount(): ApisResponse<UserDetailsResponse> {
        return try {
            val callApi = apiStories.getNotificationCount("vicky")
            ApisResponse.Success(callApi)
        } catch (e: Exception) {
            ApisResponse.Error(e.message ?: AppConstants.SOMETHING_WENT_WRONG)
        }
    }

}