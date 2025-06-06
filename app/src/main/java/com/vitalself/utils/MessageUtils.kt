package com.vitalself.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorRes
import com.vitalself.R
import com.vitalself.base.BaseActivity
import org.aviran.cookiebar2.CookieBar


object MessageUtils {
    fun showToast(context: Context, message: String?) {
        if (!message.isNullOrEmpty())
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showErrorMessage(activity: Activity?, message: String?) {
        showTopMessage(activity, message, R.color.red_text_color, true, "Error")
    }

    fun showSuccessMessage(activity: Activity?, message: String?) {
        showTopMessage(activity, message, R.color.green_text_color, false,"Success")
    }

    private fun showTopMessage(
        activity: Activity?, message: String?, @ColorRes bgColor: Int, isError: Boolean, title : String?
    ) {
        if (activity != null && !activity.isFinishing && !message.isNullOrEmpty()) {
            CookieBar.build(activity as BaseActivity)
                .setTitle(if (title != null) title else activity.getString(R.string.app_name))
                .setTitleColor(R.color.white_both_theme)
                .setMessageColor(R.color.white_both_theme)
                .setBackgroundColor(bgColor)
                .setMessage(message)
                .setIcon(0)
                .setIcon(if (isError) R.drawable.ic_close else R.drawable.ic_success)
                .setDuration(2000)
                .show()
        }
    }

}