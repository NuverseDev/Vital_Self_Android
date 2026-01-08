package com.vital_self.features.scan.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital_self.core.data.remote.model.CheckVersionRequest
import com.vital_self.core.data.remote.model.CheckVersionResponse
import com.vital_self.features.profile.data.model.GetUserResponse
import com.vital_self.features.profile.data.model.UpdateScanResponse
import com.vital_self.features.profile.data.model.UserLoginRequest
import com.vital_self.features.profile.data.model.UserRequest
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.core.utils.helpers.NetworkUtils
import com.vital_self.features.scan.data.repository.ScanRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class ScanViewModel(val repository : ScanRepository): ViewModel() {

    private val _updateData = MutableLiveData<ApiResponseState<UpdateScanResponse>?>(null)
    val updateData: MutableLiveData<ApiResponseState<UpdateScanResponse>?> = _updateData

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