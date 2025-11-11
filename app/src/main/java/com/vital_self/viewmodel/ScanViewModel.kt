package com.vital_self.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital_self.model.CheckVersionRequest
import com.vital_self.model.CheckVersionResponse
import com.vital_self.model.GetUserResponse
import com.vital_self.model.UpdateScanResponse
import com.vital_self.model.UserLoginRequest
import com.vital_self.model.UserRequest
import com.vital_self.network.ApiResponseState
import com.vital_self.network.NetworkUtils
import com.vital_self.repository.ScanRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class ScanViewModel(val repository : ScanRepository): ViewModel() {

    private val _userData = MutableLiveData<ApiResponseState<GetUserResponse>?>(null)
    val userData: MutableLiveData<ApiResponseState<GetUserResponse>?> = _userData

    private val _checkAppVersion = MutableLiveData<ApiResponseState<CheckVersionResponse>?>(null)
    val checkAppVersion: MutableLiveData<ApiResponseState<CheckVersionResponse>?> = _checkAppVersion

    private val _updateData = MutableLiveData<ApiResponseState<UpdateScanResponse>?>(null)
    val updateData: MutableLiveData<ApiResponseState<UpdateScanResponse>?> = _updateData

//    private val _updateData = MutableStateFlow<ApiResponseState<UpdateScanResponse>?>(null)
//    val updateData: StateFlow<ApiResponseState<UpdateScanResponse>?> = _updateData

    fun checkAppVersion(requestBody: CheckVersionRequest, context: Context) {
        Log.d("TAG", "getUserById: $requestBody ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                Log.d("TAG", "getUserById: 2 ")
                _checkAppVersion.value = ApiResponseState.error("No Internet Available",100)
            }
            else -> {
                Log.d("TAG", "getUserById: 3")
                _checkAppVersion.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.checkAppVersion(requestBody =requestBody).catch {
                        Log.d("TAG", "getUserById: 4 ${it.message}")
                        _checkAppVersion.value =
                            ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "getUserById: 5")
                        _checkAppVersion.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }


    fun getUser(requestBody:UserLoginRequest, context: Context) {
        Log.d("TAG", "getUserById: $requestBody ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                Log.d("TAG", "getUserById: 2 ")
                _userData.value = ApiResponseState.error("No Internet Available",100)
            }
            else -> {
                Log.d("TAG", "getUserById: 3")
                _userData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.getUser(requestBody =requestBody).catch {
                        Log.d("TAG", "getUserById: 4 ${it.message}")
                        _userData.value =
                            ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "getUserById: 5")
                        _userData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getUserById(requestBody:Int, context: Context) {
        Log.d("TAG", "getUserById: $requestBody ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                Log.d("TAG", "getUserById: 2 ")
                _userData.value = ApiResponseState.error("No Internet Available",100)
            }
            else -> {
                Log.d("TAG", "getUserById: 3")
                _userData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.getUserById(requestBody =requestBody).catch {
                        Log.d("TAG", "getUserById: 4 ${it.message}")
                        _userData.value =
                            ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "getUserById: 5")
                        _userData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun updateScan(request: UserRequest, context: Context) {
        Log.d("TAG", "getUserById: $request ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _updateData.value = ApiResponseState.error("No Internet Available",100
                )
            }
            else -> {
                _updateData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.updateScan(request =request).catch {
                        _updateData.value =
                            ApiResponseState.error(it.message, 100)
                    }.collect {
                        _updateData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }
}