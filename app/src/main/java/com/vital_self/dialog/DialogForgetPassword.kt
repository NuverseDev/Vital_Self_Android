package com.vital_self.dialog

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.vital_self.databinding.DialogForgetPasswordBinding
import com.vital_self.databinding.DialogHowToScanBinding

class DialogForgetPassword(var activity: Activity, private var listner:ForgetPasswordListener) : DialogFragment() {
   private lateinit var binding: DialogForgetPasswordBinding

    interface ForgetPasswordListener {
        fun onDismiss()
        fun onSubmit(email: String?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogForgetPasswordBinding.inflate(layoutInflater)
        dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        isCancelable = false
        dialog?.setCanceledOnTouchOutside(false)
        binding.btnCancel.setOnClickListener {
            listner.onDismiss()
            dismiss()
        }
        binding.etEmail.doAfterTextChanged {
            binding.tvEmailError.visibility = View.GONE
        }

        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (isValidEmail(email)){
                listner.onSubmit(email)
                dismiss()
            }else{
                binding.tvEmailError.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}