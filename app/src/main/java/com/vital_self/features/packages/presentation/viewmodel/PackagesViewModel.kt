package com.vital_self.features.packages.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital_self.features.packages.data.model.CreditPackagesResponse
import com.vital_self.features.packages.data.model.PaymentInitiateResponse
import com.vital_self.features.packages.data.model.PaymentVerifyResponse
import com.vital_self.features.packages.data.model.PurchaseCreditResponse
import com.vital_self.features.packages.data.model.UserPackagesResponse
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.core.utils.helpers.NetworkUtils
import com.vital_self.features.packages.data.repository.PackagesRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PackagesViewModel(private val repository: PackagesRepository) : ViewModel() {

    private val _creditPackagesData = MutableLiveData<ApiResponseState<CreditPackagesResponse>?>(null)
    val creditPackagesData: MutableLiveData<ApiResponseState<CreditPackagesResponse>?> = _creditPackagesData

    private val _userPackagesData = MutableLiveData<ApiResponseState<UserPackagesResponse>?>(null)
    val userPackagesData: MutableLiveData<ApiResponseState<UserPackagesResponse>?> = _userPackagesData

    private val _purchaseData = MutableLiveData<ApiResponseState<PurchaseCreditResponse>?>(null)
    val purchaseData: MutableLiveData<ApiResponseState<PurchaseCreditResponse>?> = _purchaseData

    private val _paymentInitiateData = MutableLiveData<ApiResponseState<PaymentInitiateResponse>?>(null)
    val paymentInitiateData: MutableLiveData<ApiResponseState<PaymentInitiateResponse>?> = _paymentInitiateData

    private val _paymentVerifyData = MutableLiveData<ApiResponseState<PaymentVerifyResponse>?>(null)
    val paymentVerifyData: MutableLiveData<ApiResponseState<PaymentVerifyResponse>?> = _paymentVerifyData

    fun getCreditPackages(context: Context) {
        Log.d("TAG", "getCreditPackages")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _creditPackagesData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _creditPackagesData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.getCreditPackages().catch {
                        Log.d("TAG", "getCreditPackages error: ${it.message}")
                        _creditPackagesData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "getCreditPackages success: ${it.data}")
                        _creditPackagesData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getUserPackages(context: Context) {
        Log.d("TAG", "getUserPackages")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _userPackagesData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _userPackagesData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.getUserPackages().catch {
                        Log.d("TAG", "getUserPackages error: ${it.message}")
                        _userPackagesData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "getUserPackages success: ${it.data}")
                        _userPackagesData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun purchaseCredit(context: Context, packageId: Int) {
        Log.d("TAG", "purchaseCredit packageId: $packageId")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _purchaseData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _purchaseData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.purchaseCredit(packageId).catch {
                        Log.d("TAG", "purchaseCredit error: ${it.message}")
                        _purchaseData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "purchaseCredit success: ${it.data}")
                        _purchaseData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun resetPurchaseState() {
        _purchaseData.value = null
    }

    fun initiatePayment(context: Context, packageId: Int) {
        Log.d("TAG", "initiatePayment packageId: $packageId")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _paymentInitiateData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _paymentInitiateData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.initiatePayment(packageId).catch {
                        Log.d("TAG", "initiatePayment error: ${it.message}")
                        _paymentInitiateData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "initiatePayment success: ${it.data}")
                        _paymentInitiateData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun verifyPayment(context: Context, reference: String) {
        Log.d("TAG", "verifyPayment reference: $reference")
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _paymentVerifyData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _paymentVerifyData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.verifyPayment(reference).catch {
                        Log.d("TAG", "verifyPayment error: ${it.message}")
                        _paymentVerifyData.value = ApiResponseState.error(it.message, 400)
                    }.collect {
                        Log.d("TAG", "verifyPayment success: ${it.data}")
                        _paymentVerifyData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun resetPaymentState() {
        _paymentInitiateData.value = null
        _paymentVerifyData.value = null
    }
}
