package com.vital_self

import android.app.Application



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

    }

}