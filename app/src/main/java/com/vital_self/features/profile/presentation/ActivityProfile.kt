package com.vital_self.features.profile.presentation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.biosensesignal.sdk.api.session.user_info.SmokingStatus
import com.vital_self.R
import com.vital_self.core.domain.model.Model
import com.vital_self.features.auth.data.model.ProfileUpdateRequest
import com.vital_self.features.auth.data.model.ProfileUpdateResponse
import com.vital_self.core.data.remote.model.Status
import com.vital_self.features.auth.data.repository.AuthRepository
import com.vital_self.features.auth.presentation.viewmodel.AuthViewModelFactory
import com.vital_self.core.ui.components.dialogs.LoadingDialog
import com.vital_self.features.profile.presentation.ProfileFieldError
import com.vital_self.features.profile.presentation.ProfileScreenContent
import com.vital_self.features.profile.presentation.ProfileScreenEvent
import com.vital_self.features.profile.presentation.ProfileScreenState
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.utils.helpers.AlertDialogManager
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.core.utils.helpers.DialogClickListener
import com.vital_self.core.data.local.preferences.PreferenceManager
import com.vital_self.features.auth.presentation.viewmodel.AuthViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivityProfile : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository())
    }

    private val title by lazy { intent.getStringExtra(TITLE) ?: "Profile" }

    companion object {
        const val TITLE = "title"

        fun startActivity(activity: Activity, title: String) {
            Intent(activity, ActivityProfile::class.java).apply {
                putExtra(TITLE, title)
            }.run {
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

        setContent {
            VitalSelfTheme(darkTheme = false) {
                // Load existing data
                val existingSubject = PreferenceManager.subjectDetails

                val existingAuthUser = PreferenceManager.authUser

                Log.d("TAG", "onCreate: auth user $existingAuthUser")

                var screenState by remember {
                    mutableStateOf(
                        ProfileScreenState(
                            name = existingSubject?.name ?: existingAuthUser?.name ?: "",
                            age = existingSubject?.age?.toInt()?.toString() ?: existingAuthUser?.age?.toString() ?: "",
                            height = formatHeightForDisplay(existingSubject?.height ?: existingAuthUser?.height ?: 0.0, existingSubject?.heightUnit ?: existingAuthUser?.heightUnit ?: "cm"),
                            heightUnit = existingSubject?.heightUnit ?: existingAuthUser?.heightUnit ?: "cm",
                            weight = formatWeightForDisplay(existingSubject?.weight ?: existingAuthUser?.weight ?: 0.0, existingSubject?.weightUnit ?: existingAuthUser?.weightUnit ?: "kg"),
                            weightUnit = existingSubject?.weightUnit ?: existingAuthUser?.weightUnit?: "kg",
                            gender = existingSubject?.sex?.let { sexToString(it) } ?: existingAuthUser?.gender ?: "",
                            isSmoker = existingSubject?.isSmoker == SmokingStatus.SMOKER || existingAuthUser?.smokerStatus == "smoker",
                            isDoctor = existingAuthUser?.doctor == true
                        )
                    )
                }

                val profileUpdateData by authViewModel.profileUpdateData.observeAsState()

                // Handle API response
                LaunchedEffect(profileUpdateData) {
                    when (profileUpdateData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(isLoading = true)
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(isLoading = false)
                            PreferenceManager.isProfileUpdated = true
                            handleProfileUpdateSuccess(profileUpdateData?.data, screenState)
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(isLoading = false)
                            showErrorDialog(profileUpdateData?.message ?: "Failed to update profile")
                            authViewModel.resetProfileUpdateState()
                        }
                        else -> {}
                    }
                }

                ProfileScreenContent(
                    state = screenState,
                    title = title,
                    onEvent = { event ->
                        when (event) {
                            is ProfileScreenEvent.NameChanged -> {
                                screenState = screenState.copy(name = event.value, showError = false, errorField = ProfileFieldError.NONE)
                            }
                            is ProfileScreenEvent.AgeChanged -> {
                                screenState = screenState.copy(age = event.value, showError = false, errorField = ProfileFieldError.NONE)
                            }
                            is ProfileScreenEvent.HeightChanged -> {
                                screenState = screenState.copy(height = event.value, showError = false, errorField = ProfileFieldError.NONE)
                            }
                            is ProfileScreenEvent.HeightUnitChanged -> {
                                val convertedHeight = convertHeight(
                                    screenState.height,
                                    screenState.heightUnit,
                                    event.value
                                )
                                screenState = screenState.copy(
                                    height = convertedHeight,
                                    heightUnit = event.value,
                                    showError = false,
                                    errorField = ProfileFieldError.NONE
                                )
                            }
                            is ProfileScreenEvent.WeightChanged -> {
                                screenState = screenState.copy(weight = event.value, showError = false, errorField = ProfileFieldError.NONE)
                            }
                            is ProfileScreenEvent.WeightUnitChanged -> {
                                val convertedWeight = convertWeight(
                                    screenState.weight,
                                    screenState.weightUnit,
                                    event.value
                                )
                                screenState = screenState.copy(
                                    weight = convertedWeight,
                                    weightUnit = event.value,
                                    showError = false,
                                    errorField = ProfileFieldError.NONE
                                )
                            }
                            is ProfileScreenEvent.GenderChanged -> {
                                screenState = screenState.copy(gender = event.value, showError = false, errorField = ProfileFieldError.NONE)
                            }
                            is ProfileScreenEvent.SmokerStatusChanged -> {
                                screenState = screenState.copy(isSmoker = event.value, showError = false, errorField = ProfileFieldError.NONE)
                            }
                            ProfileScreenEvent.SaveClicked -> {
                                val validationResult = validateProfile(screenState)
                                if (validationResult == ValidUser.VALID) {
                                    saveProfile(screenState)
                                } else {
                                    screenState = screenState.copy(
                                        showError = true,
                                        errorField = getErrorField(validationResult),
                                        errorMessage = getValidationErrorMessage(validationResult)
                                    )
                                }
                            }
                            ProfileScreenEvent.BackClicked -> {
                                finish()
                            }
                            ProfileScreenEvent.DismissError -> {
                                screenState = screenState.copy(showError = false)
                            }
                        }
                    }
                )

                // Loading Dialog
                LoadingDialog(isVisible = screenState.isLoading)
            }
        }
    }

    private fun formatHeightForDisplay(height: Double?, unit: String): String {
        if (height == null) return ""
        return if (unit == "cm") {
            height.toInt().toString()
        } else {
            String.format("%.2f", height / 30.48)
        }
    }

    private fun formatWeightForDisplay(weight: Double?, unit: String): String {
        if (weight == null) return ""
        return if (unit == "kg") {
            String.format("%.1f", weight)
        } else {
            String.format("%.1f", weight * 2.20462)
        }
    }

    private fun sexToString(sex: Sex): String {
        return when (sex) {
            Sex.MALE -> "male"
            Sex.FEMALE -> "female"
            else -> "Other"
        }
    }

    private fun stringToSex(gender: String): Sex {
        return when (gender.lowercase()) {
            "male" -> Sex.MALE
            "female" -> Sex.FEMALE
            else -> Sex.UNSPECIFIED
        }
    }

    private fun convertHeight(currentValue: String, fromUnit: String, toUnit: String): String {
        if (currentValue.isEmpty()) return ""
        val value = currentValue.toDoubleOrNull() ?: return ""

        return when {
            fromUnit == "cm" && toUnit == "ft" -> String.format("%.2f", value / 30.48)
            fromUnit == "ft" && toUnit == "cm" -> (value * 30.48).toInt().toString()
            else -> currentValue
        }
    }

    private fun convertWeight(currentValue: String, fromUnit: String, toUnit: String): String {
        if (currentValue.isEmpty()) return ""
        val value = currentValue.toDoubleOrNull() ?: return ""

        return when {
            fromUnit == "kg" && toUnit == "lb" -> String.format("%.1f", value * 2.20462)
            fromUnit == "lb" && toUnit == "kg" -> String.format("%.1f", value * 0.453592)
            else -> currentValue
        }
    }

    private fun validateProfile(state: ProfileScreenState): ValidUser {
        return when {
            state.name.isBlank() -> ValidUser.INVALID_FIRST_NAME
            state.age.isBlank() -> ValidUser.INVALID_AGE
            state.height.isBlank() -> ValidUser.INVALID_HEIGHT
            state.heightUnit.isBlank() -> ValidUser.INVALID_HEIGHT_UNIT
            state.weight.isBlank() -> ValidUser.INVALID_WEIGHT
            state.weightUnit.isBlank() -> ValidUser.INVALID_WEIGHT_UNIT
            state.gender.isBlank() -> ValidUser.INVALID_GENDER
            else -> ValidUser.VALID
        }
    }

    private fun getValidationErrorMessage(validUser: ValidUser): String {
        return when (validUser) {
            ValidUser.INVALID_FIRST_NAME -> "Please enter your name"
            ValidUser.INVALID_AGE -> "Please enter your age"
            ValidUser.INVALID_HEIGHT -> "Please enter your height"
            ValidUser.INVALID_HEIGHT_UNIT -> "Please select height unit"
            ValidUser.INVALID_WEIGHT -> "Please enter your weight"
            ValidUser.INVALID_WEIGHT_UNIT -> "Please select weight unit"
            ValidUser.INVALID_GENDER -> "Please select your gender"
            ValidUser.VALID -> ""
        }
    }

    private fun getErrorField(validUser: ValidUser): ProfileFieldError {
        return when (validUser) {
            ValidUser.INVALID_FIRST_NAME -> ProfileFieldError.NAME
            ValidUser.INVALID_AGE -> ProfileFieldError.AGE
            ValidUser.INVALID_HEIGHT, ValidUser.INVALID_HEIGHT_UNIT -> ProfileFieldError.HEIGHT
            ValidUser.INVALID_WEIGHT, ValidUser.INVALID_WEIGHT_UNIT -> ProfileFieldError.WEIGHT
            ValidUser.INVALID_GENDER -> ProfileFieldError.GENDER
            ValidUser.VALID -> ProfileFieldError.NONE
        }
    }

    private fun saveProfile(state: ProfileScreenState) {
        // Convert to standard units for storage and API
        val heightInCm = if (state.heightUnit == "ft") {
            state.height.toDoubleOrNull()?.times(30.48) ?: 0.0
        } else {
            state.height.toDoubleOrNull() ?: 0.0
        }

        val weightInKg = if (state.weightUnit == "lb") {
            state.weight.toDoubleOrNull()?.times(0.453592) ?: 0.0
        } else {
            state.weight.toDoubleOrNull() ?: 0.0
        }

        val smokerStatus = if (state.isSmoker) "smoker" else "non_smoker"

        // Save to local storage first
        val subjectData = Model.SubjectDetails(
            name = state.name,
            sex = stringToSex(state.gender),
            age = state.age.toDoubleOrNull() ?: 0.0,
            weight = weightInKg,
            height = heightInCm,
            heightUnit = state.heightUnit,
            weightUnit = state.weightUnit,
            isSmoker = if (state.isSmoker) SmokingStatus.SMOKER else SmokingStatus.NON_SMOKER
        )
        PreferenceManager.subjectDetails = subjectData

        // Call API to update profile
        val request = ProfileUpdateRequest(
            name = state.name,
            age = state.age.toIntOrNull(),
            height = heightInCm,
            heightUnit = state.heightUnit,
            weight = weightInKg,
            weightUnit = state.weightUnit,
            gender = state.gender,
            smokerStatus = smokerStatus
        )
        authViewModel.updateProfile(request, this)
    }

    private fun handleProfileUpdateSuccess(data: ProfileUpdateResponse?, state: ProfileScreenState) {
        try {
            if (data?.status == true && data.data != null) {
                // Update authUser with new data
                PreferenceManager.authUser = data.data
                Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetProfileUpdateState()
                finish()
            } else {
                // Even if API fails, local data is saved, so we can finish
                Toast.makeText(this, "Profile saved locally", Toast.LENGTH_SHORT).show()
                authViewModel.resetProfileUpdateState()
                finish()
            }
        } catch (e: Exception) {
            Log.d("TAG", "handleProfileUpdateSuccess catch: ${e.message}")
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialogManager.showConfirmationDialog(
            this,
            title = getString(R.string.error),
            message = message,
            buttonMessage = getString(R.string.ok),
            cancelable = true,
            dialogClickListener = object : DialogClickListener {
                override fun onButton1Clicked() {

                }
            }
        )
    }

    private fun roundOff(value: Double, decimals: Int): Double {
        return BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP).toDouble()
    }
}

enum class ValidUser {
    INVALID_FIRST_NAME,
    INVALID_AGE,
    INVALID_WEIGHT,
    INVALID_HEIGHT,
    INVALID_HEIGHT_UNIT,
    INVALID_WEIGHT_UNIT,
    INVALID_GENDER,
    VALID
}
