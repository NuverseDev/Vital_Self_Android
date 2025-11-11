package com.vital_self.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.vital_self.R
import com.vital_self.databinding.DialogProgressBinding

object CustomProgressDialog {
    private var progressDialog: Dialog? = null
    private var progressDialogRefresh: Dialog? = null

    fun showProgressDialog(mContext: Context?, cancelable: Boolean = false) {
        dismissProgressDialog()
        progressDialog = null
        System.gc()
        if (mContext != null) {
            progressDialog = Dialog(mContext).apply {
                val progressBinding: DialogProgressBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.dialog_progress, null, false
                )
                setContentView(progressBinding.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(cancelable)
                show()
            }
        }
    }

    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog?.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}