package com.moon.kotlin_weathercheck

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    init {
        instance = this
    }
    companion object {
        lateinit var instance : MyApplication

        fun getContext(): Context {
            return instance.applicationContext
        }
    }
}