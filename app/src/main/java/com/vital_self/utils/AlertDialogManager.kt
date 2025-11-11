package com.vital_self.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.vital_self.R
import com.vital_self.databinding.DialogConfirmBinding

object AlertDialogManager {

    fun showConfirmationDialog(
        activity: Activity,
        title: String? = null,
        message: String? = null,
        buttonMessage: String? = null,
        cancelable: Boolean = true,
        isDissable: Boolean = false,
        dialogClickListener: DialogClickListener? = null
    ) {
        Dialog(activity).apply {
            setCancelable(cancelable)
            val dialogConfirmBinding: DialogConfirmBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.dialog_confirm,
                null,
                false
            )
            setContentView(dialogConfirmBinding.root)
            dialogConfirmBinding.tvTitle.text = title
            dialogConfirmBinding.tvMessage.text = message
            dialogConfirmBinding.btnSeeResult.text = buttonMessage
            if (isDissable) dialogConfirmBinding.imgCloseDialog.visibility = View.GONE else dialogConfirmBinding.imgCloseDialog.visibility = View.VISIBLE
            dialogConfirmBinding.btnSeeResult.setOnClickListener {
                dialogClickListener?.onButton1Clicked()
                dismiss()
            }
            dialogConfirmBinding.imgCloseDialog.setOnClickListener {
                dismiss()
            }

        }.run {
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

}

interface DialogClickListener {
    fun onButton1Clicked()
}