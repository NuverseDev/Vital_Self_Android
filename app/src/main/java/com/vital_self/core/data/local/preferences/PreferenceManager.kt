package com.vital_self.core.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.vital_self.VitalSelfApplication
import com.vital_self.R
import com.vital_self.core.domain.model.Model
import com.google.gson.Gson
import com.vital_self.features.auth.data.model.AuthUser
import com.vital_self.features.profile.data.model.UserModel

object PreferenceManager {
    object PrefConstant {
        const val ISLOGGEDIN = "isloggedin"
        const val IS_FIRST_TIME = "is_first_time"
        const val IS_PROFILE_UPDATED = "is_profile_updated"
        const val SUBJECT = "subject"
        const val USER = "user"
        const val AUTH_USER = "auth_user"
        const val AUTH_TOKEN = "auth_token"
        const val KEY = "key"
        const val APP_VERSION_MATCH = "app_version_match"
        const val IS_R_PASS = "is_R_Pass"
        const val AVAILABLE_CREDITS = "available_credits"
    }

    private val prefs: SharedPreferences =
        VitalSelfApplication.getInstance().applicationContext!!.getSharedPreferences(
            VitalSelfApplication.getInstance().applicationContext.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

    var appVersionMatch : Boolean
        get() = prefs.getBoolean(PrefConstant.APP_VERSION_MATCH, true)
        set(value) = prefs.edit().putBoolean(PrefConstant.APP_VERSION_MATCH, value).apply()

    var isLoggedIn : Boolean
        get() = prefs.getBoolean(PrefConstant.ISLOGGEDIN, false)
        set(value) = prefs.edit().putBoolean(PrefConstant.ISLOGGEDIN, value).apply()

    var isProfileUpdated : Boolean
        get() = prefs.getBoolean(PrefConstant.IS_PROFILE_UPDATED, false)
        set(value) = prefs.edit().putBoolean(PrefConstant.IS_PROFILE_UPDATED, value).apply()

    var isFreshInstalled : Boolean
        get() = prefs.getBoolean(PrefConstant.IS_FIRST_TIME, true)
        set(value) = prefs.edit().putBoolean(PrefConstant.IS_FIRST_TIME, value).apply()

    var subjectDetails: Model.SubjectDetails?
        get() {
            val json = prefs.getString(PrefConstant.SUBJECT, null)
            return if (json != null) Gson().fromJson(json, Model.SubjectDetails::class.java) else null
        }
        set(value) {
            val editor = prefs.edit()
            if (value == null) {
                editor.remove(PrefConstant.SUBJECT)
            } else {
                val json = Gson().toJson(value)
                editor.putString(PrefConstant.SUBJECT, json)
            }
            editor.apply()
        }

    var user: UserModel?
        get() {
            val json = prefs.getString(PrefConstant.USER, null)
            return if (json != null) Gson().fromJson(json, UserModel::class.java) else null
        }
        set(value) {
            val editor = prefs.edit()
            if (value == null) {
                editor.remove(PrefConstant.USER)
            } else {
                val json = Gson().toJson(value)
                editor.putString(PrefConstant.USER, json)
            }
            editor.apply()
        }

    var authUser: AuthUser?
        get() {
            val json = prefs.getString(PrefConstant.AUTH_USER, null)
            return if (json != null) Gson().fromJson(json, AuthUser::class.java) else null
        }
        set(value) {
            val editor = prefs.edit()
            if (value == null) {
                editor.remove(PrefConstant.AUTH_USER)
            } else {
                val json = Gson().toJson(value)
                editor.putString(PrefConstant.AUTH_USER, json)
            }
            editor.apply()
        }

    var authToken: String?
        get() = prefs.getString(PrefConstant.AUTH_TOKEN, null)
        set(value) {
            val editor = prefs.edit()
            if (value == null) {
                editor.remove(PrefConstant.AUTH_TOKEN)
            } else {
                editor.putString(PrefConstant.AUTH_TOKEN, value)
            }
            editor.apply()
        }

    var Key: String?
        get() = prefs.getString(PrefConstant.KEY, null)
        set(value) {
            val editor = prefs.edit()
            if (value == null) {
                editor.remove(PrefConstant.KEY)
            } else {
                editor.putString(PrefConstant.KEY, value)
            }
            editor.apply()
        }

    var availableCredits: Int
        get() = prefs.getInt(PrefConstant.AVAILABLE_CREDITS, 0)
        set(value) = prefs.edit().putInt(PrefConstant.AVAILABLE_CREDITS, value).apply()

}