package com.vital_self

import android.app.Application



class MyApplication : Application() {
    companion object {
        private var singleton: MyApplication? = null
        fun getInstance(): MyApplication {
            if (singleton == null) {
                singleton = MyApplication()
            }
            return singleton!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        singleton = this

    }

}