package com.vital_self.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vital_self.R
import com.vital_self.databinding.ActivityMainBinding
import com.vital_self.viewmodel.LoginViewModel


class MainActivity : AppCompatActivity() {

    lateinit var loginViewModel: LoginViewModel

    lateinit var context: Context

    lateinit var strUsername: String
    lateinit var strPassword: String

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        context = this@MainActivity

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.btnAddLogin.setOnClickListener {

            strUsername = binding.txtUsername.text.toString().trim()
            strPassword = binding.txtPassword.text.toString().trim()

            if (strPassword.isEmpty()) {
                binding.txtUsername.error = "Please enter the username"
            }
            else if (strPassword.isEmpty()) {
                binding.txtPassword.error = "Please enter the username"
            }
            else {
                loginViewModel.insertData(context, strUsername, strPassword)
                binding.lblInsertResponse.text = "Inserted Successfully"
            }
        }

        binding.btnReadLogin.setOnClickListener {

            strUsername = binding.txtUsername.text.toString().trim()

            loginViewModel.getLoginDetails(context, strUsername)!!.observe(this, Observer {

                if (it == null) {
                    binding.lblReadResponse.text = "Data Not Found"
                    binding.lblUseraname.text = "- - -"
                    binding.lblPassword.text = "- - -"
                }
                else {
                    binding.lblUseraname.text = it.Username
                    binding.lblPassword.text = it.Password

                    binding.lblReadResponse.text = "Data Found Successfully"
                }
            })
        }
    }
}