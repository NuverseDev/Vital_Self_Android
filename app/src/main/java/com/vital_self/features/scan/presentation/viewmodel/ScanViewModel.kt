package com.vital_self.features.scan.presentation.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.biosensesignal.sdk.api.session.user_info.SmokingStatus
import com.biosensesignal.sdk.api.vital_signs.VitalSignTypes
import com.biosensesignal.sdk.api.vital_signs.VitalSignsResults
import com.biosensesignal.sdk.api.vital_signs.vitals.PNSZone
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignASCVDRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignBloodPressure
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignCardiacWorkload
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHeartAge
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHemoglobinA1C
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHighBloodPressureRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHighFastingGlucoseRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHighHemoglobinA1CRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHighTotalCholesterolRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignLFHF
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignLowHemoglobinRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignMeanArterialPressure
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignMeanRRI
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPNSIndex
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPNSZone
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPRQ
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPulsePressure
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPulseRate
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignRMSSD
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignRespirationRate
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSD1
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSD2
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSDNN
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSNSIndex
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSNSZone
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignStressLevel
import com.vital_self.core.data.local.preferences.PreferenceManager
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.core.data.remote.model.AppVersion
import com.vital_self.core.data.remote.model.CheckVersionResponse
import com.vital_self.core.data.remote.model.Status
import com.vital_self.core.utils.helpers.NetworkUtils
import com.vital_self.features.packages.data.model.AvailableCreditsResponse
import com.vital_self.core.domain.model.Model
import com.vital_self.features.history.data.model.Gender
import com.vital_self.features.history.data.model.HeightUnit
import com.vital_self.features.history.data.model.SaveScanHistoryRequest
import com.vital_self.features.history.data.model.SmokerUnit
import com.vital_self.features.history.data.model.WeightUnit
import com.vital_self.features.history.data.repository.HistoryRepository
import com.vital_self.features.profile.data.model.UpdateScanResponse
import com.vital_self.features.profile.data.model.UserRequest
import com.vital_self.features.scan.data.repository.ScanRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// Sealed class for scan result states
sealed class ScanResultState {
    object Idle : ScanResultState()
    object Loading : ScanResultState()
    data class Success(val scanId: Int) : ScanResultState()
    data class Error(val message: String?) : ScanResultState()
    object ValidationError : ScanResultState()
}

// Sealed class for credit check states
sealed class CreditCheckState {
    object Idle : CreditCheckState()
    object Loading : CreditCheckState()
    data class Success(val availableCredits: Int) : CreditCheckState()
    object NoCredits : CreditCheckState()
    data class Error(val message: String?) : CreditCheckState()
}

class ScanViewModel(val repository: ScanRepository) : ViewModel() {

    private val _updateData = MutableLiveData<ApiResponseState<UpdateScanResponse>?>(null)
    val updateData: MutableLiveData<ApiResponseState<UpdateScanResponse>?> = _updateData

    // Scan result processing state
    private val _appVersion = MutableLiveData<ApiResponseState<CheckVersionResponse>?>(null)
    val appVersion: MutableLiveData<ApiResponseState<CheckVersionResponse>?> = _appVersion


    // Scan result processing state
    private val _scanResult = MutableLiveData<ScanResultState>(ScanResultState.Idle)
    val scanResult: LiveData<ScanResultState> = _scanResult

    // Credit check state
    private val _creditCheckState = MutableLiveData<CreditCheckState>(CreditCheckState.Idle)
    val creditCheckState: LiveData<CreditCheckState> = _creditCheckState

    fun updateScan(request: UserRequest, context: Context) {
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _updateData.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _updateData.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.updateScan(request = request).catch {
                        _updateData.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        _updateData.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    /**
     * Process scan results from SDK - main entry point called by Activity
     * @param isDoctorModeSelected true if doctor selected Patient mode
     * @param patientDetails patient info from bottom sheet (only when doctor + Patient mode)
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun processScanResults(
        finalResults: VitalSignsResults?,
        isDoctorModeSelected: Boolean = false,
        patientDetails: Model.SubjectDetails? = null,
        patientEmail: String? = null
    ) {
        val userProfile = resolveUserProfile(isDoctorModeSelected, patientDetails, patientEmail)
        val request = createSaveScanHistoryRequest(finalResults, userProfile)

        if (!validateScanResults(request)) {
            _scanResult.postValue(ScanResultState.ValidationError)
            return
        }
        saveScanHistory(request)
    }

    /**
     * Resolve which user profile data to include in the request:
     * - Normal user / Doctor+Self: all profile fields empty
     * - Doctor+Patient: use patient info collected from bottom sheet
     */
    private fun resolveUserProfile(
        isDoctorModeSelected: Boolean,
        patientDetails: Model.SubjectDetails?,
        patientEmail: String?
    ): UserProfileData {
        return if (isDoctorModeSelected && patientDetails != null) {
            // Doctor + Patient mode: use bottom sheet patient info
            UserProfileData(
                name = patientDetails.name,
                email = patientEmail,
                age = patientDetails.age?.toInt(),
                sex = when (patientDetails.sex) {
                    Sex.MALE -> Gender.male
                    Sex.FEMALE -> Gender.female
                    else -> null
                },
                weight = patientDetails.weight,
                weightUnit = when (patientDetails.weightUnit) {
                    "lb" -> WeightUnit.LB
                    else -> WeightUnit.KG
                },
                height = patientDetails.height,
                heightUnit = when (patientDetails.heightUnit) {
                    "ft" -> HeightUnit.FT
                    else -> HeightUnit.CM
                },
                smoker = when (patientDetails.isSmoker) {
                    SmokingStatus.SMOKER -> SmokerUnit.smoker
                    SmokingStatus.NON_SMOKER -> SmokerUnit.non_smoker
                    else -> SmokerUnit.non_smoker
                }
            )
        } else {
            // Normal user or Doctor+Self: send empty profile fields
            UserProfileData(
                name = null,
                email = null,
                age = null,
                sex = null,
                weight = null,
                weightUnit = null,
                height = null,
                heightUnit = null,
                smoker = null
            )
        }
    }

    private data class UserProfileData(
        val name: String?,
        val email: String?,
        val age: Int?,
        val sex: Gender?,
        val weight: Double?,
        val weightUnit: WeightUnit?,
        val height: Double?,
        val heightUnit: HeightUnit?,
        val smoker: SmokerUnit?
    )

    /**
     * Validate that required vital signs are present
     */
    private fun validateScanResults(request: SaveScanHistoryRequest): Boolean {
        return (request.heartRate ?: 0) != 0 &&
                (request.bloodPressureSystolic ?: 0) != 0 &&
                (request.bloodPressureDiastolic ?: 0) != 0 &&
                (request.oxygenSaturation ?: 0) != 0 &&
                (request.prq ?: 0.0) != 0.0 &&
                (request.breathingRate ?: 0) != 0
    }

    /**
     * Save scan history to API
     */
    private fun saveScanHistory(request: SaveScanHistoryRequest) {
        _scanResult.postValue(ScanResultState.Loading)
        viewModelScope.launch {
            HistoryRepository.saveScanHistoryToApi(request).collect { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        val scanId = response.data?.data?.scanHistory?.id
                        _scanResult.postValue(ScanResultState.Success(scanId ?: -1))
                    }
                    Status.ERROR -> {
                        _scanResult.postValue(ScanResultState.Error(response.message))
                    }
                    Status.LOADING -> {
                        // Already set to loading
                    }
                }
            }
        }
    }
    fun checkAppVersion (context: Context){
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _appVersion.value = ApiResponseState.error("No Internet Available", 100)
            }
            else -> {
                _appVersion.value = ApiResponseState.loading()
                viewModelScope.launch {
                    repository.checkAppVersion().catch {
                        _appVersion.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        _appVersion.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    /**
     * Reset scan result state to idle
     */

    fun resetScanResultState() {
        _scanResult.postValue(ScanResultState.Idle)
    }

    /**
     * Check available credits from API
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkAvailableCredits(context: Context) {
        when {
            (!NetworkUtils.isNetworkAvailable(context)) -> {
                _creditCheckState.value = CreditCheckState.Error("No Internet Available")
            }
            else -> {
                _creditCheckState.value = CreditCheckState.Loading
                viewModelScope.launch {
                    repository.getAvailableCredits().catch {
                        _creditCheckState.value = CreditCheckState.Error(it.message)
                    }.collect { response ->
                        when (response.status) {
                            Status.SUCCESS -> {
                                val credits = response.data?.data?.totalAvailableCredits ?: 0
                                // Save to SharedPreferences
                                PreferenceManager.availableCredits = credits
                                if (credits > 0) {
                                    _creditCheckState.value = CreditCheckState.Success(credits)
                                } else {
                                    _creditCheckState.value = CreditCheckState.NoCredits
                                }
                            }
                            Status.ERROR -> {
                                _creditCheckState.value = CreditCheckState.Error(response.message)
                            }
                            Status.LOADING -> {
                                // Already set to loading
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Reset credit check state to idle
     */
    fun resetCreditCheckState() {
        _creditCheckState.postValue(CreditCheckState.Idle)
    }

    /**
     * Create SaveScanHistoryRequest from SDK VitalSignsResults
     * Moved from VitalScanActivity
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun createSaveScanHistoryRequest(finalResults: VitalSignsResults?, userProfile: UserProfileData): SaveScanHistoryRequest {
        // Extract heart rate
        val heartRateN = finalResults?.getResult(VitalSignTypes.PULSE_RATE) as VitalSignPulseRate
        val heartRate = heartRateN.value
        val heartRateLevel = (finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.ordinal ?: 0

        // Extract breathing rate

        val breathingRate = finalResults.getResult(VitalSignTypes.RESPIRATION_RATE)?.value as? Int ?: 0
        val breathingRateLevel = (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.ordinal ?: 0

        // Extract oxygen saturation
        val oxygenSaturation = finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION)?.value as? Int ?: 0


        // Extract SDNN
        val sdnn = finalResults.getResult(VitalSignTypes.SDNN)?.value as? Int ?: 0
        val sdnnLevel = (finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.ordinal ?: 0

        // Extract stress level

        val stressLevel = (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel).value.ordinal
        // Extract blood pressure
        val bpResult = finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as? VitalSignBloodPressure
        val bloodPressureSystolic = bpResult?.value?.systolic ?: 0
        val bloodPressureDiastolic = bpResult?.value?.diastolic ?: 0

        // Extract hemoglobin
        val hemoglobin = (finalResults.getResult(VitalSignTypes.HEMOGLOBIN)?.value as? Number)?.toDouble() ?: 0.0

        // Extract hemoglobin A1C
        val hemoglobinA1c = (finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value as? Number)?.toDouble() ?: 0.0

        // Extract risks
        val diabetesRisk = finalResults.getResult(VitalSignTypes.HIGH_HEMOGLOBIN_A1C_RISK)?.value as? Int ?: 0
        val hypertensionRisk = finalResults.getResult(VitalSignTypes.HIGH_BLOOD_PRESSURE_RISK)?.value as? Int ?: 0

        // Extract wellness
        val wellnessIndex = finalResults.getResult(VitalSignTypes.WELLNESS_INDEX)?.value as? Int ?: 0
        val wellnessLevel = finalResults.getResult(VitalSignTypes.WELLNESS_LEVEL)?.value?.toString() ?: ""

        // Extract PRQ
        val prq = (finalResults.getResult(VitalSignTypes.PRQ)?.value as? Number)?.toDouble() ?: 0.0
        val prqLevel = (finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.ordinal ?: 0

        // Extract recovery and stress response

        val recoveryAbility = (finalResults.getResult(VitalSignTypes.PNS_ZONE) as VitalSignPNSZone).value.ordinal


        val stressResponse =  (finalResults.getResult(VitalSignTypes.SNS_ZONE) as VitalSignSNSZone).value.ordinal

        // Extract SNS/PNS indices
        val snsIndex = (finalResults.getResult(VitalSignTypes.SNS_INDEX) as? VitalSignSNSIndex)?.value ?: 0.0
        val pnsIndex = (finalResults.getResult(VitalSignTypes.PNS_INDEX) as? VitalSignPNSIndex)?.value ?: 0.0

        // Extract LF/HF
        val lfhf = (finalResults.getResult(VitalSignTypes.LFHF) as? VitalSignLFHF)?.value ?: 0.0

        // Extract SD1, SD2
        val sd1 = (finalResults.getResult(VitalSignTypes.SD1) as? VitalSignSD1)?.value ?: 0
        val sd2 = (finalResults.getResult(VitalSignTypes.SD2) as? VitalSignSD2)?.value ?: 0

        // Extract RMSSD
        val rmssd = (finalResults.getResult(VitalSignTypes.RMSSD) as? VitalSignRMSSD)?.value ?: 0

        // Extract Mean RRI
        val meanRri = (finalResults.getResult(VitalSignTypes.MEAN_RRI) as? VitalSignMeanRRI)?.value ?: 0

        // Extract additional risks
        val highBloodPressureRisk = (finalResults.getResult(VitalSignTypes.HIGH_BLOOD_PRESSURE_RISK) as? VitalSignHighBloodPressureRisk)?.value?.ordinal
        val highHemoglobinA1cRisk = (finalResults.getResult(VitalSignTypes.HIGH_HEMOGLOBIN_A1C_RISK) as? VitalSignHighHemoglobinA1CRisk)?.value?.ordinal
        val highFastingGlucoseRisk = (finalResults.getResult(VitalSignTypes.HIGH_FASTING_GLUCOSE_RISK) as? VitalSignHighFastingGlucoseRisk)?.value?.ordinal ?: 0
        val highTotalCholesterolRisk = (finalResults.getResult(VitalSignTypes.HIGH_TOTAL_CHOLESTEROL_RISK) as? VitalSignHighTotalCholesterolRisk)?.value?.ordinal ?: 0
        val lowHemoglobinRisk = (finalResults.getResult(VitalSignTypes.LOW_HEMOGLOBIN_RISK) as? VitalSignLowHemoglobinRisk)?.value?.ordinal ?: 0

        // Extract ASCVD risk and heart age
        val ascvdRisk = (finalResults.getResult(VitalSignTypes.ASCVD_RISK) as? VitalSignASCVDRisk)?.value ?: 0.0
        val heartAge = (finalResults.getResult(VitalSignTypes.HEART_AGE) as? VitalSignHeartAge)?.value ?: 0
        val ascvdRiskLevel = (finalResults.getResult(VitalSignTypes.ASCVD_RISK) as? VitalSignASCVDRisk)?.value ?: 0.0

        // Calculate derived values
        val cardiacWorkload =  (finalResults.getResult(VitalSignTypes.CARDIAC_WORKLOAD) as? VitalSignCardiacWorkload)?.value ?: 0.0
        val pulsePressure =  (finalResults.getResult(VitalSignTypes.PULSE_PRESSURE) as? VitalSignPulsePressure)?.value ?: 0
        val meanArterialPressure =  (finalResults.getResult(VitalSignTypes.MEAN_ARTERIAL_PRESSURE) as? VitalSignMeanArterialPressure)?.value ?: 0

        val requestResponse = SaveScanHistoryRequest(
            name = userProfile.name,
            email = userProfile.email,
            age = userProfile.age,
            sex = userProfile.sex,
            weight = userProfile.weight,
            weightUnit = userProfile.weightUnit,
            height = userProfile.height,
            heightUnit = userProfile.heightUnit,
            smokerStatus = userProfile.smoker,
            heartRate = heartRate,
            heartRateLevel = heartRateLevel,
            breathingRate = breathingRate,
            breathingRateLevel = breathingRateLevel,
            oxygenSaturation = oxygenSaturation,
            sdnn = sdnn,
            sdnnLevel = sdnnLevel,
            stressLevel = stressLevel,
            bloodPressureSystolic = bloodPressureSystolic,
            bloodPressureDiastolic = bloodPressureDiastolic,
            hemoglobin = hemoglobin,
            hemoglobinA1c = hemoglobinA1c,
            diabetesRisk = diabetesRisk,
            hypertensionRisk = hypertensionRisk,
            wellnessIndex = wellnessIndex,
            wellnessLevel = wellnessLevel,
            prq = prq,
            prqLevel = prqLevel,
            recoveryAbility = recoveryAbility,
            stressResponse = stressResponse,
            snsIndex = snsIndex,
            pnsIndex = pnsIndex,
            lfhf = lfhf,
            sd1 = sd1,
            sd2 = sd2,
            rmssd = rmssd,
            meanRri = meanRri,
            highBloodPressureRisk = highBloodPressureRisk,
            highHemoglobinA1cRisk = highHemoglobinA1cRisk,
            highFastingGlucoseRisk = highFastingGlucoseRisk,
            highTotalCholesterolRisk = highTotalCholesterolRisk,
            lowHemoglobinRisk = lowHemoglobinRisk,
            ascvdRisk = ascvdRisk,
            heartAge = heartAge,
            ascvdRiskLevel = ascvdRiskLevel,
            cardiacWorkload = cardiacWorkload,
            pulsePressure = pulsePressure,
            meanArterialPressure = meanArterialPressure
        )

        return requestResponse
    }
}
