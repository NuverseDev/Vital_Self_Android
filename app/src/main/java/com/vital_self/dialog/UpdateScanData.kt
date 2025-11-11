package com.vital_self.dialog

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.vital_self.databinding.DialogHowToScanBinding

class UpdateScanData(var activity: Activity, private var listner:OnClick) : DialogFragment() {
   private lateinit var binding: DialogHowToScanBinding
    interface OnClick {
        fun onDismiss()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogHowToScanBinding.inflate(layoutInflater)
        dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        isCancelable = false
        dialog?.setCanceledOnTouchOutside(false)
        binding.btnGo.setOnClickListener {
            listner.onDismiss()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}