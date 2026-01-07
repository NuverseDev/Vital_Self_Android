package com.vital_self.utils

import android.view.View

fun View.showHideError(isErrorEnable: Boolean) {
    if (isErrorEnable) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}