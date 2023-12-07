package com.app.mybase


import com.app.mybase.base.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {

}