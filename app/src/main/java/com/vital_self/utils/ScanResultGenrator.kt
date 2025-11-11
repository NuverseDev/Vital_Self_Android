package com.vital_self.utils

import android.content.Context
import android.util.Log
import com.vital_self.R
import com.vital_self.model.Model
import com.vital_self.view.MeasurementResult

object ScanResultGenrator {

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
                result.ascvd.toDouble().toInt() < 1-> Constant.LOW
                result.ascvd.toDouble().toInt() in 1..30 -> Constant.NORMAL
                result.ascvd.toDouble().toInt() > 30 -> Constant.HIGH
                else -> Constant.UNKNOWN
            }
        }else{
            ascvdStatus = Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.ASCVD,
                vitalValue = "${result.ascvd}" ,
                vitalUnit = Constant.ASCVD_UNIT,
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
                vitalName = Constant.HEART_AGE,
                vitalValue = "${result.heartAge}" ,
                vitalUnit = Constant.HEART_AGE_UNIT,
                vitalIcon = R.drawable.ic_vital_scanning_prq,
                vitalStatus = Constant.UNKNOWN ,
                vitalDetail = context.getString(R.string.heart_age_desc),
                confidenceLevel = null,
                dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.RISK.name,
                no_of_state = 0
            ))

        val hypertensionRiskStatus: Int = when {
            result.hypertensionRisk ==  "NORMAL"-> Constant.NORMAL
            result.hypertensionRisk ==  "LOW" -> Constant.LOW
            result.hypertensionRisk ==  "HIGH" -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojiStatusHyperTension: Int = when {
            result.hypertensionRisk ==  "NORMAL"-> Constant.NOT_GOOD
            result.hypertensionRisk ==  "LOW" -> Constant.GOOD
            result.hypertensionRisk ==  "HIGH" -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.HIGH_BLOOD_PRESSURE_RISK,
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
            result.diabeticRisk ==  "MEDIUM"-> Constant.MEDIUM
            result.diabeticRisk ==  "LOW" -> Constant.LOW
            result.diabeticRisk ==  "HIGH" -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojiDiabeticRiskStatus: Int = when {
            result.diabeticRisk ==  "NORMAL"-> Constant.NOT_GOOD
            result.diabeticRisk ==  "LOW" -> Constant.GOOD
            result.diabeticRisk ==  "MEDIUM" -> Constant.NOT_GOOD
            result.diabeticRisk ==  "HIGH" -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.HIGH_HEMOGLOBIN_A1C_RISK,
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
            Constant.HIGH -> "High"
            Constant.NORMAL -> "Normal"
            Constant.MEDIUM -> "Low"
            else -> ""
        }


        val highFastingGlucoseStatus = when(result.highFastingGlucose.toInt()){
            Constant.HIGH -> Constant.HIGH
            Constant.NORMAL -> Constant.NORMAL
            Constant.LOW -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojiHighFastingGlucoseStatus = when(result.highFastingGlucose.toInt()){
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.GOOD
            else -> Constant.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.HIGH_FIASTING_GLUCOSE,
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
            Constant.HIGH -> "HIGH"
            Constant.NORMAL -> "Normal"
            Constant.LOW -> "Low"
            else -> "NA"
        }

        val totalColestrolRiskStatus = when(result.totalColestrol.toInt()){
            Constant.HIGH -> Constant.HIGH
            Constant.NORMAL -> Constant.NORMAL
            Constant.LOW -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojiTotalColestrolRiskStatus = when(result.totalColestrol.toInt()){
            Constant.HIGH -> Constant.BAD
            Constant.NORMAL -> Constant.NOT_GOOD
            Constant.LOW -> Constant.GOOD
            else -> Constant.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.TOTAL_COLESTROL,
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
            Constant.MEDIUM -> "HIGH"
            Constant.LOW -> "LOW"
            else -> "NA"
        }

        val lowhemoglobinRiskStatus = when(result.lowHemoglobinRisk.toInt()){
            Constant.MEDIUM -> Constant.HIGH
            Constant.LOW -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojilowhemoglobinRiskStatus = when(lowhemoglobinRiskStatus){
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.GOOD
            else -> Constant.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.LOW_HEMOGLOBIN_RISK,
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
            result.heartRate.toInt() in 60..100 -> Constant.NORMAL
            result.heartRate.toInt() > 100 -> Constant.HIGH
            result.heartRate.toInt() < 60 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojiHeartRateStatus: Int = when(heartRateStatus) {
             Constant.NORMAL -> Constant.GOOD
             Constant.HIGH -> Constant.BAD
             Constant.LOW -> Constant.NORMAL_GOOD
            else -> Constant.UNKNOWN
        }



        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.HEART_RATE,
                vitalValue = result.heartRate,
                vitalUnit = Constant.HEART_RATE_UNIT,
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
            result.breathingRate.toInt() in 12..20 -> Constant.NORMAL
            result.breathingRate.toInt() > 20 -> Constant.HIGH
            result.breathingRate.toInt() < 12 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojibreathingRateStatus: Int = when(breathingRateStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.NOT_GOOD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.BREATHING_RATE,
                vitalValue = result.breathingRate ,
                vitalUnit = Constant.BREATHING_RATE_UNIT,
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
            result.PRQ.toDouble() in 3.0..5.0 -> Constant.NORMAL
            result.PRQ.toDouble() > 5.0 -> Constant.HIGH
            result.PRQ.toDouble() < 3.0 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojiprqStatus: Int = when(prqStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.PRQ,
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
            result.bloodPressureSystolic.toInt() in 100..129 -> Constant.NORMAL
            result.bloodPressureSystolic.toInt() >= 130 -> Constant.HIGH
            result.bloodPressureSystolic.toInt() < 100 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojibloodPressureStatus: Int = when(bloodPressureStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.NOT_GOOD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.BLOOD_PRESSURE,
                vitalValue = "${result.bloodPressureSystolic}/${result.bloodPressureDistolic}" ,
                vitalUnit = Constant.BLOOD_PRESSURE_UNIT,
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
            result.oxygenSat.toInt() >= 96 -> Constant.NORMAL
            result.oxygenSat.toInt() in 91..93 -> Constant.NOT_GOOD
            result.oxygenSat.toInt() <= 90 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojioxygenSaturationStatus: Int = when(oxygenSaturationStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.BLOW -> Constant.NOT_GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.OXYGEN_SATURATION,
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
            result.hemoglobinA1c.toDouble() in 4.0..5.6 -> Constant.NORMAL
            result.hemoglobinA1c.toDouble() in 5.7..6.4 -> Constant.LOW
            result.hemoglobinA1c.toDouble() >= 6.5 -> Constant.HIGH
            else -> Constant.UNKNOWN
        }
        val emojiHemoglobinA1cStatus: Int = when {
            result.hemoglobinA1c.toDouble() in 4.0..5.6 -> Constant.GOOD
            result.hemoglobinA1c.toDouble() in 5.7..6.4 -> Constant.NOT_GOOD
            result.hemoglobinA1c.toDouble() >= 6.5 -> Constant.NOT_GOOD
            else -> Constant.UNKNOWN
        }


        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.HEMOGLOBIN_A1C,
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
                result.hemoglobin.toDouble() < 14.0 -> Constant.LOW
                result.hemoglobin.toDouble() in 13.5..18.0 -> Constant.NORMAL
                result.hemoglobin.toDouble() > 18 -> Constant.HIGH
                else -> Constant.UNKNOWN
            }
            "Female" -> when {
                result.hemoglobin.toDouble() < 12 -> Constant.LOW
                result.hemoglobin.toDouble() in 12.0..16.0 -> Constant.NORMAL
                result.hemoglobin.toDouble() > 16 -> Constant.HIGH
                else -> Constant.UNKNOWN
            }
            else -> Constant.UNKNOWN
        }

        val emojihaemoglobinStatus: Int = when(haemoglobinStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.GOOD
            Constant.LOW -> Constant.NORMAL_GOOD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.HEMOGLOBIN,
                vitalValue = result.hemoglobin ,
                vitalUnit = Constant.HEMOGLOBIN_UNIT,
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
            result.stressLevel ==  "NORMAL"-> Constant.NORMAL
            result.stressLevel ==  "LOW" -> Constant.LOW
            result.stressLevel ==  "HIGH" -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojistressLevelStatus: Int = when(stressLevelStatus) {
            Constant.NORMAL -> Constant.NOT_GOOD
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.GOOD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.STRESS_LEVEL,
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
            result.hrv_sdnn.toInt() > 100 -> Constant.NORMAL
            result.hrv_sdnn.toInt() in 50..100 -> Constant.NORMAL
            result.hrv_sdnn.toInt()  in 20..50 -> Constant.MEDIUM
            result.hrv_sdnn.toInt() < 20 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojihrvSdnnStatus: Int = when(hrvSdnnStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.MEDIUM -> Constant.NORMAL_GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.HRV_SDNN,
                vitalValue = result.hrv_sdnn ,
                vitalUnit = Constant.HRV_SDNN_UNIT,
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
            result.recoveryRate ==  "NORMAL"-> Constant.NORMAL
            result.recoveryRate ==  "LOW" -> Constant.LOW
            result.recoveryRate ==  "HIGH" -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojirecoveryAbilityStatus: Int = when(recoveryAbilityStatus) {
            Constant.NORMAL -> Constant.NOT_GOOD
            Constant.HIGH -> Constant.GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.RECOVERY_ABILITY,
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
            result.stressResp ==  "NORMAL"-> Constant.NORMAL
            result.stressResp ==  "LOW" -> Constant.LOW
            result.stressResp ==  "HIGH" -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojistressRespStatus: Int = when(stressRespStatus) {
            Constant.NORMAL -> Constant.NOT_GOOD
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.GOOD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.STRESS_RESPONSE,
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
            result.meanRri in 600..1000 -> Constant.NORMAL
            result.meanRri > 1000 -> Constant.HIGH
            result.meanRri < 600 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojimeanRriValueStatus: Int = when(meanRriValueStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.MEAN_RRI,
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
            result.pnsIndex > 2.5 -> Constant.HIGH
            result.pnsIndex in 1.5..2.5 -> Constant.NORMAL
            result.pnsIndex < 1.5 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojipnsStatus: Int = when(pnsStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.PNS,
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
            result.snsIndex > 2.5 -> Constant.HIGH
            result.snsIndex in 1.5..2.5 -> Constant.HIGH
            result.snsIndex in -1.4..1.4 -> Constant.NORMAL
            result.snsIndex < -1.5 -> Constant.LOW
            else -> Constant.UNKNOWN
        }

        val emojisnsStatus: Int = when(snsStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.BAD
            Constant.LOW -> Constant.NORMAL_GOOD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.SNS,
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
            result.RMMSD < 20 -> Constant.LOW
            result.RMMSD in 20..50 -> Constant.NORMAL
            result.RMMSD > 43 -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojirmssdStatus: Int = when(rmssdStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.RMSSD,
                vitalValue = result.RMMSD.toString() ,
                vitalUnit = Constant.HRV_SDNN_UNIT,
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
            normalizedSD1 < 20 -> Constant.LOW
            normalizedSD1 in 20.0..50.0 -> Constant.NORMAL
            normalizedSD1 > 50 -> Constant.HIGH
            else -> Constant.UNKNOWN
        }
        val emojisd1Status: Int = when(rmssdStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }
        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.SD1,
                vitalValue = result.sd1.toString() ,
                vitalUnit = Constant.HRV_SDNN_UNIT,
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
            normalizedSD2 < 40 -> Constant.LOW
            normalizedSD2 in 40.0..84.0 -> Constant.NORMAL
            normalizedSD2 > 84 -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojisd2Status: Int = when(rmssdStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.GOOD
            Constant.LOW -> Constant.BAD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.SD2,
                vitalValue = result.sd2.toString() ,
                vitalUnit = Constant.HRV_SDNN_UNIT,
                vitalIcon = R.drawable.ic_vital_sd2,
                vitalStatus = sd2Status ,
                vitalDetail = context.getString(R.string.sd2_description),
                confidenceLevel = null,  dialogType = DIALOG_TYPE.BASIC.name,
                category = VITAL_CATEGORY.ADVANCE_HRV.name,
                emojiStatus = emojisd2Status
            ))

        val lfHfStatus: Int = when {
            result.lfhf < 0.5 -> Constant.LOW
            result.lfhf in 0.5..2.0 -> Constant.NORMAL
            result.lfhf > 2.0 -> Constant.HIGH
            else -> Constant.UNKNOWN
        }

        val emojilfHfStatus: Int = when(lfHfStatus) {
            Constant.NORMAL -> Constant.GOOD
            Constant.HIGH -> Constant.NOT_GOOD
            Constant.LOW -> Constant.NOT_GOOD
            else -> Constant.UNKNOWN
        }

        vitalList.add(
            Model.VitalsData(
                vitalName = Constant.LFHF,
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