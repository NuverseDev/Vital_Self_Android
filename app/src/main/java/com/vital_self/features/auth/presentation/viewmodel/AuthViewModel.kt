package com.vital_self.features.auth.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital_self.features.auth.data.model.AuthResponse
import com.vital_self.core.data.remote.model.CheckVersionRequest
import com.vital_self.core.data.remote.model.CheckVersionResponse
import com.vital_self.features.auth.data.model.ForgotPasswordRequest
import com.vital_self.features.auth.data.model.ForgotPasswordResponse
import com.vital_self.features.profile.data.model.GetUserResponse
import com.vital_self.features.auth.data.model.LoginRequest
import com.vital_self.features.auth.data.model.ProfileUpdateRequest
import com.vital_self.features.auth.data.model.ProfileUpdateResponse
import com.vital_self.features.auth.data.model.SignupRequest
import com.vital_self.features.profile.data.model.UpdateScanResponse
import com.vital_self.features.profile.data.model.UserLoginRequest
import com.vital_self.features.profile.data.model.UserRequest
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.core.utils.helpers.NetworkUtils
import com.vital_self.features.auth.data.repository.AuthRepository
import com.vital_self.features.scan.data.repository.ScanRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class AuthViewModel(val repository: AuthRepository) : ViewModel() {

    private val _loginData = MutableLiveData<ApiResponseState<AuthResponse>?>(null)
    val loginData: MutableLiveData<ApiResponseState<AuthResponse>?> = _loginData

    private val _signupData = MutableLiveData<ApiResponseState<AuthResponse>?>(null)
    val signupData: MutableLiveData<ApiResponseState<AuthResponse>?> = _signupData

    private val _forgotPasswordData = MutableLiveData<ApiResponseState<ForgotPasswordResponse>?>(null)
    val forgotPasswordData: MutableLiveData<ApiResponseState<ForgotPasswordResponse>?> = _forgotPasswordData

    private val _profileUpdateData = MutableLiveData<ApiResponseState<ProfileUpdateResponse>?>(null)
    val profileUpdateData: MutableLiveData<ApiResponseState<ProfileUpdateResponse>?> = _profileUpdateData

    private val _userData = MutableLiveData<ApiResponseState<GetUserResponse>?>(null)
    val userData: MutableLiveData<ApiResponseState<GetUserResponse>?> = _userData

    private val _checkAppVersion = MutableLiveData<ApiResponseState<CheckVersionResponse>?>(null)
    val checkAppVersion: MutableLiveData<ApiResponseState<CheckVersionResponse>?> = _checkAppVersion

    fun loginUser(requestBody: LoginRequest, context: Context) {
        Log.d("TAG", "loginUser: $requestBody")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _loginData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _loginData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.login(requestBody = requestBody).catch {
                        Log.d("TAG", "loginUser error: ${it.message}")
                        _loginData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "loginUser success: ${it.data}")
                        _loginData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun signupUser(requestBody: SignupRequest, context: Context) {
        Log.d("TAG", "signupUser: $requestBody")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _signupData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _signupData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.signup(requestBody = requestBody).catch {
                        Log.d("TAG", "signupUser error: ${it.message}")
                        _signupData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "signupUser success: ${it.data}")
                        _signupData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun forgotPassword(requestBody: ForgotPasswordRequest, context: Context) {
        Log.d("TAG", "forgotPassword: $requestBody")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _forgotPasswordData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _forgotPasswordData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.forgotPassword(requestBody = requestBody).catch {
                        Log.d("TAG", "forgotPassword error: ${it.message}")
                        _forgotPasswordData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "forgotPassword success: ${it.data}")
                        _forgotPasswordData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun resetForgotPasswordState() {
        _forgotPasswordData.value = null
    }

    fun updateProfile(requestBody: ProfileUpdateRequest, context: Context) {
        Log.d("TAG", "updateProfile: $requestBody")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _profileUpdateData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _profileUpdateData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.updateProfile(requestBody = requestBody).catch {
                        Log.d("TAG", "updateProfile error: ${it.message}")
                        _profileUpdateData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "updateProfile success: ${it.data}")
                        _profileUpdateData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun resetProfileUpdateState() {
        _profileUpdateData.value = null
    }

    fun checkAppVersion(requestBody: CheckVersionRequest, context: Context) {
        Log.d("TAG", "getUserById: $requestBody ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                Log.d("TAG", "getUserById: 2 ")
                _checkAppVersion.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                Log.d("TAG", "getUserById: 3")
                _checkAppVersion.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.checkAppVersion(requestBody = requestBody).catch {
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

    fun login(requestBody: UserLoginRequest, context: Context) {
        Log.d("TAG", "getUserById: $requestBody ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                Log.d("TAG", "getUserById: 2 ")
                _userData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                Log.d("TAG", "getUserById: 3")
                _userData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.getUser(requestBody = requestBody).catch {
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

    fun getUserById(requestBody: Int, context: Context) {
        Log.d("TAG", "getUserById: $requestBody ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                Log.d("TAG", "getUserById: 2 ")
                _userData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                Log.d("TAG", "getUserById: 3")
                _userData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.getUserById(requestBody = requestBody).catch {
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

    fun registerUser(requestBody: Int, context: Context) {
        Log.d("TAG", "getUserById: $requestBody ")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                Log.d("TAG", "getUserById: 2 ")
                _userData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                Log.d("TAG", "getUserById: 3")
                _userData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.getUserById(requestBody = requestBody).catch {
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

}