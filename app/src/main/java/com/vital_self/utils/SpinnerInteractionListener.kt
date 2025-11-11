package com.vital_self.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView

abstract class SpinnerInteractionListener : AdapterView.OnItemSelectedListener, OnTouchListener {
    var userSelect = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        userSelect = true
        return false
    }

}