package com.app.mybase


import androidx.lifecycle.liveData
import com.app.mybase.base.BaseViewModel
import com.app.mybase.helper.ApisResponse
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {


    fun getName(): String {
        return mainRepository.getName()
    }

    fun getNotificationCount() = liveData(Dispatchers.IO) {
        emit(ApisResponse.Loading)
        emit(mainRepository.getNotificationCount())
        emit(ApisResponse.Complete)
    }

}