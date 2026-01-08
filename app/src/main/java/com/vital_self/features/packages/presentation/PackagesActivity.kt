package com.vital_self.features.packages.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

    companion object {
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

                // Handle Purchase API response
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
                            // Refresh user packages to show the new purchase
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
                                    packagesViewModel.purchaseCredit(this@PackagesActivity, pkg.id)
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
}
