package com.example.sportsstore

import android.app.Application
import android.content.Context
import com.example.sportsstore.LocaleHelper

class MyApp : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocaleHelper.setLocale(it, LocaleHelper.getLocale(it)) })
    }
}