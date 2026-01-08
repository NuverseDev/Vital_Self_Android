package com.vital_self.core.utils.constants

import okhttp3.internal.platform.Platform

object AppConstants {
    const val SUBJECT = "subject"
    const val VITAL_LIST = "vital_list"
    const val HEART_RATE = "Heart Rate"
    const val HEART_RATE_UNIT = "bpm"
    const val DATE = "date"
    const val TIME = "time"

    const val BREATHING_RATE = "Breathing Rate"
    const val BREATHING_RATE_UNIT = "brpm"

    const val PRQ = "PRQ"


    const val OXYGEN_SATURATION = "Oxygen Saturation"


    const val BLOOD_PRESSURE = "Blood Pressure"
    const val BLOOD_PRESSURE_UNIT = "mmHg"

    const val HRV_SDNN = "HRV-SDNN"
    const val HRV_SDNN_UNIT = "ms"

    const val HEART_AGE_UNIT = "Yrs"

    const val ASCVD_UNIT = "%"



    const val HEMOGLOBIN = "Hemoglobin"
    const val HEMOGLOBIN_UNIT = "g/dl"

    const val HEMOGLOBIN_A1C = "Hemoglobin A1C"

    const val STRESS_LEVEL = "Stress Level"

    const val STRESS_RESPONSE = "Stress Response"

    const val RECOVERY_ABILITY = "Recovery Ability"

    const val HIGH_BLOOD_PRESSURE_RISK = "High Blood Pressure Risk"

    const val HIGH_HEMOGLOBIN_A1C_RISK = "high Hemoglobin A1c Risk"

    const val ASCVD = "ASCVD risk"

    const val HIGH_FIASTING_GLUCOSE = "High fasting glucose risk"

    const val HEART_AGE = "Heart age"

    const val LOW_HEMOGLOBIN_RISK = "Low hemoglobin"

    const val TOTAL_COLESTROL = "High total cholesterol"

    const val WELLNESS_INDEX = "Wellness Score"

    const val WELLNESS_LEVEL = "Wellness Level"

    const val MEAN_RRI = "Mean RRi"

    const val PNS = "PNS Index"

    const val SNS = "SNS Index"

    const val RMSSD = "RMSSD"
    const val SD1 = "SD1"
    const val SD2 = "SD2"
    const val LFHF = "LF/HF"

    const val HIGH = 3
    const val NORMAL = 2
    const val LOW = 0
    const val MEDIUM = 1
    const val BLOW = 4
    const val UNKNOWN = 0
    const val NORMAL_GOOD = 4

    const val GOOD = 1
    const val NOT_GOOD = 2
    const val BAD = 3

    const val PROFILE = "Profile"
    const val HISTORY = "History"
    const val PACKAGES = "Packages"
    const val BEST_PRACTICES = "Best Practices"

    const val PLATFORM = "android"
    const val APP_NAME = "vitalself"
}

enum class DIALOG_TYPE {
    BASIC,
    RANGE,
    STATE
}

enum class VITAL_CATEGORY{
    RISK,
    VITAL_SIGN,
    BLOOD,
    BLOOD_TEST,
    STRESS,
    HRV,
    ENERGY,
    ADVANCE_HRV
}