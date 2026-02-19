package com.vital_self

import android.app.Application
//import co.paystack.android.PaystackSdk


class VitalSelfApplication : Application() {
    companion object {
        private var singleton: VitalSelfApplication? = null
        fun getInstance(): VitalSelfApplication {
            if (singleton == null) {
                singleton = VitalSelfApplication()
            }
            return singleton!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        singleton = this
//        PaystackSdk.initialize(getApplicationContext());
//        PaystackSdk.setPublicKey("pk_live_52f3535ead29c46c3aedc33e6533db9b74c670de")
    }

}