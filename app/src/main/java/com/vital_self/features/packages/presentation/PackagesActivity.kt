package com.vital_self.features.packages.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vital_self.core.data.remote.model.Status
import com.vital_self.features.billing.PaymentActivity
import com.vital_self.features.packages.data.repository.PackagesRepository
import com.vital_self.features.packages.presentation.viewmodel.PackagesViewModelFactory
import com.vital_self.features.packages.presentation.PackagesScreenContent
import com.vital_self.features.packages.presentation.PackagesScreenEvent
import com.vital_self.features.packages.presentation.PackagesScreenState
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.features.packages.presentation.viewmodel.PackagesViewModel

class PackagesActivity : ComponentActivity() {

    private val packagesViewModel: PackagesViewModel by viewModels {
        PackagesViewModelFactory(PackagesRepository())
    }

    private var pendingReference: String? = null

    companion object {
        private const val PAYMENT_REQUEST_CODE = 1001

        fun startActivity(activity: Activity) {
            Intent(activity, PackagesActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Fetch data on create
        packagesViewModel.getCreditPackages(this)
        packagesViewModel.getUserPackages(this)

        setContent {
            VitalSelfTheme(darkTheme = false) {
                var screenState by remember { mutableStateOf(PackagesScreenState()) }

                val creditPackagesData by packagesViewModel.creditPackagesData.observeAsState()
                val userPackagesData by packagesViewModel.userPackagesData.observeAsState()
                val purchaseData by packagesViewModel.purchaseData.observeAsState()
                val paymentInitiateData by packagesViewModel.paymentInitiateData.observeAsState()
                val paymentVerifyData by packagesViewModel.paymentVerifyData.observeAsState()

                // Handle Credit Packages API response
                LaunchedEffect(creditPackagesData) {
                    when (creditPackagesData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(
                                isLoadingPackages = true,
                                packagesError = null
                            )
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(
                                isLoadingPackages = false,
                                creditPackages = creditPackagesData?.data?.data?.creditPackages ?: emptyList(),
                                packagesError = null
                            )
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(
                                isLoadingPackages = false,
                                packagesError = creditPackagesData?.message ?: "Failed to load packages"
                            )
                        }
                        else -> {}
                    }
                }

                // Handle User Packages API response
                LaunchedEffect(userPackagesData) {
                    when (userPackagesData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(
                                isLoadingUserPackages = true,
                                userPackagesError = null
                            )
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(
                                isLoadingUserPackages = false,
                                userPackages = userPackagesData?.data?.data?.userCredits ?: emptyList(),
                                userPackagesError = null
                            )
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(
                                isLoadingUserPackages = false,
                                userPackagesError = userPackagesData?.message ?: "Failed to load purchases"
                            )
                        }
                        else -> {}
                    }
                }

                // Handle Purchase API response (kept for backward compatibility)
                LaunchedEffect(purchaseData) {
                    when (purchaseData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(
                                isPurchasing = true,
                                purchaseError = null
                            )
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(
                                isPurchasing = false,
                                showPurchaseDialog = false,
                                purchaseSuccess = true,
                                purchaseError = null
                            )
                            packagesViewModel.getUserPackages(this@PackagesActivity)
                            packagesViewModel.resetPurchaseState()
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(
                                isPurchasing = false,
                                purchaseError = purchaseData?.message ?: "Failed to purchase package"
                            )
                        }
                        else -> {}
                    }
                }

                // Handle Payment Initiate API response
                LaunchedEffect(paymentInitiateData) {
                    when (paymentInitiateData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(
                                isPurchasing = true,
                                purchaseError = null
                            )
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(
                                isPurchasing = false,
                                showPurchaseDialog = false,
                                purchaseError = null
                            )
                            val data = paymentInitiateData?.data?.data
                            if (data != null) {
                                pendingReference = data.reference
                                PaymentActivity.startActivity(
                                    this@PackagesActivity,
                                    data.authorizationUrl,
                                    data.reference,
                                    PAYMENT_REQUEST_CODE
                                )
                            }
                            packagesViewModel.resetPaymentState()
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(
                                isPurchasing = false,
                                purchaseError = paymentInitiateData?.message ?: "Failed to initiate payment"
                            )
                            packagesViewModel.resetPaymentState()
                        }
                        else -> {}
                    }
                }

                // Handle Payment Verify API response
                LaunchedEffect(paymentVerifyData) {
                    when (paymentVerifyData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(
                                isPurchasing = true,
                                purchaseError = null
                            )
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(
                                isPurchasing = false,
                                purchaseSuccess = true,
                                purchaseError = null
                            )
                            packagesViewModel.getUserPackages(this@PackagesActivity)
                            packagesViewModel.resetPaymentState()
                            pendingReference = null
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(
                                isPurchasing = false,
                                purchaseError = paymentVerifyData?.message ?: "Payment verification failed"
                            )
                            packagesViewModel.resetPaymentState()
                            pendingReference = null
                        }
                        else -> {}
                    }
                }

                PackagesScreenContent(
                    state = screenState,
                    onEvent = { event ->
                        when (event) {
                            PackagesScreenEvent.BackClicked -> {
                                finish()
                            }
                            PackagesScreenEvent.RefreshPackages -> {
                                packagesViewModel.getCreditPackages(this@PackagesActivity)
                            }
                            PackagesScreenEvent.RefreshUserPackages -> {
                                packagesViewModel.getUserPackages(this@PackagesActivity)
                            }
                            is PackagesScreenEvent.PackageClicked -> {
                                screenState = screenState.copy(
                                    showPurchaseDialog = true,
                                    selectedPackage = event.creditPackage
                                )
                            }
                            PackagesScreenEvent.ConfirmPurchase -> {
                                screenState.selectedPackage?.let { pkg ->
                                    packagesViewModel.initiatePayment(this@PackagesActivity, pkg.id)
                                }
                            }
                            PackagesScreenEvent.DismissPurchaseDialog -> {
                                screenState = screenState.copy(
                                    showPurchaseDialog = false,
                                    selectedPackage = null,
                                    purchaseError = null
                                )
                            }
                            PackagesScreenEvent.DismissSuccessDialog -> {
                                screenState = screenState.copy(
                                    purchaseSuccess = false
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    @Deprecated("Use Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val reference = data?.getStringExtra(PaymentActivity.EXTRA_REFERENCE)
                    ?: pendingReference
                if (reference != null) {
                    packagesViewModel.verifyPayment(this, reference)
                }
            } else {
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show()
                pendingReference = null
            }
        }
    }
}
