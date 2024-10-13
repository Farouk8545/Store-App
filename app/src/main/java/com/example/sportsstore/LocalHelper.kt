package com.example.sportsstore

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val locale = when (language) {
            "English" -> Locale.ENGLISH
            "Spanish" -> Locale("es")
            "French" -> Locale.FRENCH
            "German" -> Locale.GERMAN
            "Arabic" -> Locale("ar")  // Correct Arabic locale
            else -> Locale.ENGLISH
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
            return context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            return context
        }
    }

    fun getLocale(context: Context): String {
        val sharedPref = context.getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        return sharedPref.getString("app_language", "English") ?: "English"
    }
}