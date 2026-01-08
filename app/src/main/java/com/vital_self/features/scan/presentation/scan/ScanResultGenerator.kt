package com.vital_self.features.scan.presentation.scan

import android.content.Context
import android.util.Log
import com.vital_self.R
import com.vital_self.core.domain.model.Model
import com.vital_self.features.scan.presentation.scan.MeasurementResult
import com.vital_self.core.utils.constants.AppConstants
import com.vital_self.core.utils.constants.DIALOG_TYPE
import com.vital_self.core.utils.constants.VITAL_CATEGORY

object ScanResultGenerator {

    fun createScanResult(
        context: Context,
        result: MeasurementResult // List of VitalsData
    ): ArrayList<Model.VitalsData>{

        Log.d("TAG", "createScanResult: vital result $result")

        val vitalList = ArrayList<Model.VitalsData>()

        /** ---------------- RISK ---------------------- */

        var ascvdStatus : Int? = null
        if (result.ascvd.isNotEmpty() && result.ascvd != context.getString(R.string.n_a)){
            ascvdStatus = when {
                result.ascvd.toDouble().toInt() < 1-> AppConstants.LOW
                result.ascvd.toDouble().toInt() in 1..30 -> AppConstants.NORMAL
                result.ascvd.toDouble().toInt() > 30 -> AppConstants.HIGH
                else -> AppConstants.UNKNOWN
            }
        }else{
            ascvdStatus = AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.ASCVD,
                vitalValue = "${result.ascvd}" ,
                vitalUnit = AppConstants.ASCVD_UNIT,
                vitalIcon = R.drawable.ic_vital_scanning_heart_rate,
                vitalStatus = ascvdStatus ,
                vitalDetail = context.getString(R.string.ascvd_risk_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 0
            ))



        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HEART_AGE,
                vitalValue = "${result.heartAge}" ,
                vitalUnit = AppConstants.HEART_AGE_UNIT,
                vitalIcon = R.drawable.ic_vital_scanning_prq,
                vitalStatus = AppConstants.UNKNOWN ,
                vitalDetail = context.getString(R.string.heart_age_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 0
            ))

        val hypertensionRiskStatus: Int = when {
            result.hypertensionRisk ==  "NORMAL"-> AppConstants.NORMAL
            result.hypertensionRisk ==  "LOW" -> AppConstants.LOW
            result.hypertensionRisk ==  "HIGH" -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojiStatusHyperTension: Int = when {
            result.hypertensionRisk ==  "NORMAL"-> AppConstants.NOT_GOOD
            result.hypertensionRisk ==  "LOW" -> AppConstants.GOOD
            result.hypertensionRisk ==  "HIGH" -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HIGH_BLOOD_PRESSURE_RISK,
                vitalValue = result.hypertensionRisk ,
                vitalUnit = null,
                vitalIcon =R.drawable.ic_vital_scanning_wellness_score,
                vitalStatus = hypertensionRiskStatus ,
                vitalDetail = context.getString(R.string.hypertension_risk_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 3,
                emojiStatus = emojiStatusHyperTension
            ))

        val diabeticRiskStatus: Int = when {
            result.diabeticRisk ==  "MEDIUM"-> AppConstants.MEDIUM
            result.diabeticRisk ==  "LOW" -> AppConstants.LOW
            result.diabeticRisk ==  "HIGH" -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojiDiabeticRiskStatus: Int = when {
            result.diabeticRisk ==  "NORMAL"-> AppConstants.NOT_GOOD
            result.diabeticRisk ==  "LOW" -> AppConstants.GOOD
            result.diabeticRisk ==  "MEDIUM" -> AppConstants.NOT_GOOD
            result.diabeticRisk ==  "HIGH" -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HIGH_HEMOGLOBIN_A1C_RISK,
                vitalValue = result.diabeticRisk ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_blood_pressure_scanning,
                vitalStatus = diabeticRiskStatus ,
                vitalDetail = context.getString(R.string.diabetes_risk_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 3,
                emojiStatus = emojiDiabeticRiskStatus
            ))


        val highFastingGlucoseValue = when(result.highFastingGlucose.toInt()){
            AppConstants.HIGH -> "High"
            AppConstants.NORMAL -> "Normal"
            AppConstants.MEDIUM -> "Low"
            else -> ""
        }


        val highFastingGlucoseStatus = when(result.highFastingGlucose.toInt()){
            AppConstants.HIGH -> AppConstants.HIGH
            AppConstants.NORMAL -> AppConstants.NORMAL
            AppConstants.LOW -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojiHighFastingGlucoseStatus = when(result.highFastingGlucose.toInt()){
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.GOOD
            else -> AppConstants.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HIGH_FIASTING_GLUCOSE,
                vitalValue = "$highFastingGlucoseValue" ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_hba1a,
                vitalStatus = highFastingGlucoseStatus ,
                vitalDetail = context.getString(R.string.high_fasting_glucose_risk_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 2,
                emojiStatus = emojiHighFastingGlucoseStatus
            ))



        val totalColestrolRiskValue = when(result.totalColestrol.toInt()){
            AppConstants.HIGH -> "HIGH"
            AppConstants.NORMAL -> "Normal"
            AppConstants.LOW -> "Low"
            else -> "NA"
        }

        val totalColestrolRiskStatus = when(result.totalColestrol.toInt()){
            AppConstants.HIGH -> AppConstants.HIGH
            AppConstants.NORMAL -> AppConstants.NORMAL
            AppConstants.LOW -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojiTotalColestrolRiskStatus = when(result.totalColestrol.toInt()){
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.NORMAL -> AppConstants.NOT_GOOD
            AppConstants.LOW -> AppConstants.GOOD
            else -> AppConstants.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.TOTAL_COLESTROL,
                vitalValue = "$totalColestrolRiskValue" ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_hemoglobin,
                vitalStatus = totalColestrolRiskStatus ,
                vitalDetail = context.getString(R.string.high_total_cholesterol_risk_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 3,
                emojiStatus = emojiTotalColestrolRiskStatus
            ))


        val lowhemoglobinRiskValue = when(result.lowHemoglobinRisk.toInt()){
            AppConstants.MEDIUM -> "HIGH"
            AppConstants.LOW -> "LOW"
            else -> "NA"
        }

        val lowhemoglobinRiskStatus = when(result.lowHemoglobinRisk.toInt()){
            AppConstants.MEDIUM -> AppConstants.HIGH
            AppConstants.LOW -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojilowhemoglobinRiskStatus = when(lowhemoglobinRiskStatus){
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.GOOD
            else -> AppConstants.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.LOW_HEMOGLOBIN_RISK,
                vitalValue = "$lowhemoglobinRiskValue" ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_hemoglobin,
                vitalStatus = lowhemoglobinRiskStatus ,
                vitalDetail = context.getString(R.string.low_hemoglobin_risk_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 2,
                emojiStatus = emojilowhemoglobinRiskStatus
            ))



        /** ------------------ VITAL SIGNS ----------------------- */

        val heartRateStatus: Int = when {
            result.heartRate.toInt() in 60..100 -> AppConstants.NORMAL
            result.heartRate.toInt() > 100 -> AppConstants.HIGH
            result.heartRate.toInt() < 60 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojiHeartRateStatus: Int = when(heartRateStatus) {
             AppConstants.NORMAL -> AppConstants.GOOD
             AppConstants.HIGH -> AppConstants.BAD
             AppConstants.LOW -> AppConstants.NORMAL_GOOD
            else -> AppConstants.UNKNOWN
        }



        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HEART_RATE,
                vitalValue = result.heartRate,
                vitalUnit = AppConstants.HEART_RATE_UNIT,
                vitalIcon = R.drawable.ic_vital_scanning_heart_rate,
                vitalStatus = heartRateStatus ,
                vitalDetail = context.getString(R.string.heart_rate_desc),
                confidenceLevel = result.heartRateConfi,
                isExpanded = false,
                dialogType = DIALOG_TYPE.RANGE.name,
                category = VITAL_CATEGORY.VITAL_SIGN.name,
                range = arrayListOf("40","60","100","240"),
                emojiStatus = emojiHeartRateStatus
            ))

        val breathingRateStatus: Int = when {
            result.breathingRate.toInt() in 12..20 -> AppConstants.NORMAL
            result.breathingRate.toInt() > 20 -> AppConstants.HIGH
            result.breathingRate.toInt() < 12 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojibreathingRateStatus: Int = when(breathingRateStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.NOT_GOOD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.BREATHING_RATE,
                vitalValue = result.breathingRate ,
                vitalUnit = AppConstants.BREATHING_RATE_UNIT,
                vitalIcon = R.drawable.ic_vital_scanning_breathing_rate,
                vitalStatus = breathingRateStatus ,
                vitalDetail = context.getString(R.string.respiration_desc),
                confidenceLevel = result.breathingRateConfid,
                dialogType = DIALOG_TYPE.RANGE.name,
                category = VITAL_CATEGORY.VITAL_SIGN.name,
                range = arrayListOf("6","12","20","40"),
                emojiStatus = emojibreathingRateStatus
            ))

        val prqStatus: Int = when {
            result.PRQ.toDouble() in 3.0..5.0 -> AppConstants.NORMAL
            result.PRQ.toDouble() > 5.0 -> AppConstants.HIGH
            result.PRQ.toDouble() < 3.0 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojiprqStatus: Int = when(prqStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.PRQ,
                vitalValue = result.PRQ ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_prq,
                vitalStatus = prqStatus ,
                vitalDetail = context.getString(R.string.prq_description),
                confidenceLevel = result.prqConfi,
                dialogType = DIALOG_TYPE.RANGE.name,
                category = VITAL_CATEGORY.VITAL_SIGN.name,
                range = arrayListOf("","4","5",""),
                emojiStatus = emojiprqStatus
            ))


        /** -------------------------- BLOOD ------------------------ */

        val bloodPressureStatus: Int = when {
            result.bloodPressureSystolic.toInt() in 100..129 -> AppConstants.NORMAL
            result.bloodPressureSystolic.toInt() >= 130 -> AppConstants.HIGH
            result.bloodPressureSystolic.toInt() < 100 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojibloodPressureStatus: Int = when(bloodPressureStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.NOT_GOOD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.BLOOD_PRESSURE,
                vitalValue = "${result.bloodPressureSystolic}/${result.bloodPressureDistolic}" ,
                vitalUnit = AppConstants.BLOOD_PRESSURE_UNIT,
                vitalIcon = R.drawable.ic_vital_blood_pressure_scanning,
                vitalStatus = bloodPressureStatus ,
                vitalDetail = context.getString(R.string.blood_pressure_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.RANGE.name,
                category = VITAL_CATEGORY.BLOOD.name,
                range = arrayListOf("systolic range","100","130",""),
                emojiStatus = emojibloodPressureStatus
            ))

        val oxygenSaturationStatus: Int = when {
            result.oxygenSat.toInt() >= 96 -> AppConstants.NORMAL
            result.oxygenSat.toInt() in 91..93 -> AppConstants.NOT_GOOD
            result.oxygenSat.toInt() <= 90 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojioxygenSaturationStatus: Int = when(oxygenSaturationStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.BLOW -> AppConstants.NOT_GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.OXYGEN_SATURATION,
                vitalValue = "${result.oxygenSat} %" ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_oxygen_saturation_scanning_gray,
                vitalStatus = oxygenSaturationStatus ,
                vitalDetail = context.getString(R.string.oxygen_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.RANGE.name,
                category = VITAL_CATEGORY.BLOOD.name,
                range = arrayListOf("0","95","100",""),
                emojiStatus = emojioxygenSaturationStatus
            ))


        /**  ---------------------- BLOOD TEST -------------- */


        val hemoglobinA1cStatus: Int = when {
            result.hemoglobinA1c.toDouble() in 4.0..5.6 -> AppConstants.NORMAL
            result.hemoglobinA1c.toDouble() in 5.7..6.4 -> AppConstants.LOW
            result.hemoglobinA1c.toDouble() >= 6.5 -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }
        val emojiHemoglobinA1cStatus: Int = when {
            result.hemoglobinA1c.toDouble() in 4.0..5.6 -> AppConstants.GOOD
            result.hemoglobinA1c.toDouble() in 5.7..6.4 -> AppConstants.NOT_GOOD
            result.hemoglobinA1c.toDouble() >= 6.5 -> AppConstants.NOT_GOOD
            else -> AppConstants.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HEMOGLOBIN_A1C,
                vitalValue = result.hemoglobinA1c ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_hemoglobin,
                vitalStatus = hemoglobinA1cStatus ,
                vitalDetail = context.getString(R.string.hemoglobin_a1c_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.BLOOD_TEST.name,
                emojiStatus = emojiHemoglobinA1cStatus
            ))

        val haemoglobinStatus: Int = when (result.gender) {
            "Male" -> when {
                result.hemoglobin.toDouble() < 14.0 -> AppConstants.LOW
                result.hemoglobin.toDouble() in 13.5..18.0 -> AppConstants.NORMAL
                result.hemoglobin.toDouble() > 18 -> AppConstants.HIGH
                else -> AppConstants.UNKNOWN
            }
            "Female" -> when {
                result.hemoglobin.toDouble() < 12 -> AppConstants.LOW
                result.hemoglobin.toDouble() in 12.0..16.0 -> AppConstants.NORMAL
                result.hemoglobin.toDouble() > 16 -> AppConstants.HIGH
                else -> AppConstants.UNKNOWN
            }
            else -> AppConstants.UNKNOWN
        }

        val emojihaemoglobinStatus: Int = when(haemoglobinStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.GOOD
            AppConstants.LOW -> AppConstants.NORMAL_GOOD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HEMOGLOBIN,
                vitalValue = result.hemoglobin ,
                vitalUnit = AppConstants.HEMOGLOBIN_UNIT,
                vitalIcon = R.drawable.ic_vital_scanning_hemoglobin,
                vitalStatus = haemoglobinStatus ,
                vitalDetail = context.getString(R.string.hemoglobin_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.BLOOD_TEST.name,
                emojiStatus = emojihaemoglobinStatus
            ))


        /** -----------------STRESS ----------------- */

        val stressLevelStatus: Int = when {
            result.stressLevel ==  "NORMAL"-> AppConstants.NORMAL
            result.stressLevel ==  "LOW" -> AppConstants.LOW
            result.stressLevel ==  "HIGH" -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojistressLevelStatus: Int = when(stressLevelStatus) {
            AppConstants.NORMAL -> AppConstants.NOT_GOOD
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.GOOD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.STRESS_LEVEL,
                vitalValue = result.stressLevel ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_stress_level,
                vitalStatus = stressLevelStatus ,
                vitalDetail = context.getString(R.string.stress_level_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.STRESS.name,
                no_of_state = 3,
                emojiStatus = emojistressLevelStatus
            ))

        /**  ------------------------- HRV ------------------- */

        val hrvSdnnStatus: Int = when {
            result.hrv_sdnn.toInt() > 100 -> AppConstants.NORMAL
            result.hrv_sdnn.toInt() in 50..100 -> AppConstants.NORMAL
            result.hrv_sdnn.toInt()  in 20..50 -> AppConstants.MEDIUM
            result.hrv_sdnn.toInt() < 20 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojihrvSdnnStatus: Int = when(hrvSdnnStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.MEDIUM -> AppConstants.NORMAL_GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.HRV_SDNN,
                vitalValue = result.hrv_sdnn ,
                vitalUnit = AppConstants.HRV_SDNN_UNIT,
                vitalIcon = R.drawable.ic_vital_scanning_hrv,
                vitalStatus = hrvSdnnStatus ,
                vitalDetail = context.getString(R.string.hrv_sdnn_desc),
                confidenceLevel = result.sdnCofi,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.HRV.name,
                range = arrayListOf("","50","",""),
                emojiStatus = emojihrvSdnnStatus
            ))


        /** ----------------- ENERGY --------------- */

        val recoveryAbilityStatus: Int = when {
            result.recoveryRate ==  "NORMAL"-> AppConstants.NORMAL
            result.recoveryRate ==  "LOW" -> AppConstants.LOW
            result.recoveryRate ==  "HIGH" -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojirecoveryAbilityStatus: Int = when(recoveryAbilityStatus) {
            AppConstants.NORMAL -> AppConstants.NOT_GOOD
            AppConstants.HIGH -> AppConstants.GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.RECOVERY_ABILITY,
                vitalValue = result.recoveryRate ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_recovery_ability,
                vitalStatus = recoveryAbilityStatus ,
                vitalDetail = context.getString(R.string.recovery_ability_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.ENERGY.name,
                no_of_state = 3,
                emojiStatus = emojirecoveryAbilityStatus
            ))


        val stressRespStatus: Int = when {
            result.stressResp ==  "NORMAL"-> AppConstants.NORMAL
            result.stressResp ==  "LOW" -> AppConstants.LOW
            result.stressResp ==  "HIGH" -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojistressRespStatus: Int = when(stressRespStatus) {
            AppConstants.NORMAL -> AppConstants.NOT_GOOD
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.GOOD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.STRESS_RESPONSE,
                vitalValue = result.stressResp ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_scanning_strss_response,
                vitalStatus = stressRespStatus ,
                vitalDetail = context.getString(R.string.stress_response_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.STATE.name,
                category = VITAL_CATEGORY.ENERGY.name,
                no_of_state = 3,
                emojiStatus = emojistressRespStatus
            ))

        /** ---------------- ADVANCE HRV -----------------  */

        val meanRriValueStatus: Int = when {
            result.meanRri in 600..1000 -> AppConstants.NORMAL
            result.meanRri > 1000 -> AppConstants.HIGH
            result.meanRri < 600 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojimeanRriValueStatus: Int = when(meanRriValueStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.MEAN_RRI,
                vitalValue = result.meanRri.toString() ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_mean_rri,
                vitalStatus = meanRriValueStatus ,
                vitalDetail = context.getString(R.string.mean_rri_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ADVANCE_HRV.name,
                range = arrayListOf("","600","1000",""),
                emojiStatus = emojimeanRriValueStatus
            ))


        /** --------------------------------------------   */

        val pnsStatus: Int = when {
            result.pnsIndex > 2.5 -> AppConstants.HIGH
            result.pnsIndex in 1.5..2.5 -> AppConstants.NORMAL
            result.pnsIndex < 1.5 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojipnsStatus: Int = when(pnsStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.PNS,
                vitalValue = result.pnsIndex.toString() ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_pns,
                vitalStatus = pnsStatus ,
                vitalDetail = context.getString(R.string.pns_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ENERGY.name,
                emojiStatus = emojipnsStatus
            ))

        val snsStatus: Int = when {
            result.snsIndex > 2.5 -> AppConstants.HIGH
            result.snsIndex in 1.5..2.5 -> AppConstants.HIGH
            result.snsIndex in -1.4..1.4 -> AppConstants.NORMAL
            result.snsIndex < -1.5 -> AppConstants.LOW
            else -> AppConstants.UNKNOWN
        }

        val emojisnsStatus: Int = when(snsStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.BAD
            AppConstants.LOW -> AppConstants.NORMAL_GOOD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.SNS,
                vitalValue = result.snsIndex.toString() ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_sns,
                vitalStatus = snsStatus ,
                vitalDetail = context.getString(R.string.sns_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ENERGY.name,
                range = arrayListOf("","-1","1",""),
                emojiStatus = emojisnsStatus
            ))

        val rmssdStatus: Int = when {
            result.RMMSD < 20 -> AppConstants.LOW
            result.RMMSD in 20..50 -> AppConstants.NORMAL
            result.RMMSD > 43 -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojirmssdStatus: Int = when(rmssdStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.RMSSD,
                vitalValue = result.RMMSD.toString() ,
                vitalUnit = AppConstants.HRV_SDNN_UNIT,
                vitalIcon = R.drawable.ic_vital_rmssd,
                vitalStatus = rmssdStatus ,
                vitalDetail = context.getString(R.string.rmssd_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ENERGY.name,
                range = arrayListOf("","25","43",""),
                emojiStatus = emojirmssdStatus
            ))


        val normalizedSD1 = calculateNormalizedSD1(result.sd1.toDouble(), result.sd2.toDouble())

        val sd1Status: Int = when {
            normalizedSD1 < 20 -> AppConstants.LOW
            normalizedSD1 in 20.0..50.0 -> AppConstants.NORMAL
            normalizedSD1 > 50 -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }
        val emojisd1Status: Int = when(rmssdStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }
        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.SD1,
                vitalValue = result.sd1.toString() ,
                vitalUnit = AppConstants.HRV_SDNN_UNIT,
                vitalIcon = R.drawable.ic_vital_sd1,
                vitalStatus = sd1Status ,
                vitalDetail = context.getString(R.string.sd1_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ENERGY.name,
                emojiStatus = emojisd1Status
            ))

        val normalizedSD2 = calculateNormalizedSD2(result.sd1.toDouble(), result.sd2.toDouble())

        val sd2Status: Int = when {
            normalizedSD2 < 40 -> AppConstants.LOW
            normalizedSD2 in 40.0..84.0 -> AppConstants.NORMAL
            normalizedSD2 > 84 -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojisd2Status: Int = when(rmssdStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.GOOD
            AppConstants.LOW -> AppConstants.BAD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.SD2,
                vitalValue = result.sd2.toString() ,
                vitalUnit = AppConstants.HRV_SDNN_UNIT,
                vitalIcon = R.drawable.ic_vital_sd2,
                vitalStatus = sd2Status ,
                vitalDetail = context.getString(R.string.sd2_description),
                confidenceLevel = null,  dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ADVANCE_HRV.name,
                emojiStatus = emojisd2Status
            ))

        val lfHfStatus: Int = when {
            result.lfhf < 0.5 -> AppConstants.LOW
            result.lfhf in 0.5..2.0 -> AppConstants.NORMAL
            result.lfhf > 2.0 -> AppConstants.HIGH
            else -> AppConstants.UNKNOWN
        }

        val emojilfHfStatus: Int = when(lfHfStatus) {
            AppConstants.NORMAL -> AppConstants.GOOD
            AppConstants.HIGH -> AppConstants.NOT_GOOD
            AppConstants.LOW -> AppConstants.NOT_GOOD
            else -> AppConstants.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = AppConstants.LFHF,
                vitalValue = result.lfhf.toString() ,
                vitalUnit = null,
                vitalIcon = R.drawable.ic_vital_lf_hf,
                vitalStatus = lfHfStatus ,
                vitalDetail = context.getString(R.string.lf_hf_description),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ADVANCE_HRV.name,
                emojiStatus = emojilfHfStatus
            ))
        return  vitalList
    }

    fun calculateNormalizedSD1(sd1: Double, sd2: Double): Double {
        return (sd1 / (sd1 + sd2)) * 100
    }

    fun calculateNormalizedSD2(sd1: Double, sd2: Double): Double {
        return (sd2 / (sd1 + sd2)) * 100
    }
}