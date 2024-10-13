// LanguageSettingsFragment.kt
package com.example.sportsstore.fragments.settingcomponentfragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sportsstore.R
import com.example.sportsstore.LocaleHelper

class LanguageSettingsFragment : Fragment(R.layout.fragment_language_setting) {

    private lateinit var languageSpinner: Spinner
    private val languages = listOf("English", "Spanish", "French", "German", "Arabic")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languageSpinner = view.findViewById(R.id.spinner_language)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Load saved language
        val savedLanguage = LocaleHelper.getLocale(requireContext())
        val languageIndex = languages.indexOf(savedLanguage)
        if (languageIndex != -1) {
            languageSpinner.setSelection(languageIndex)
        }

        languageSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = languages[position]
                if (selectedLanguage != LocaleHelper.getLocale(requireContext())) {
                    saveLanguage(selectedLanguage)
                    Toast.makeText(requireContext(), "Language set to $selectedLanguage", Toast.LENGTH_SHORT).show()
                    // Restart activity to apply changes
                    requireActivity().recreate()
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                // Do nothing
            }
        })
    }

    private fun saveLanguage(language: String) {
        val sharedPref = requireActivity().getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("app_language", language)
            apply()
        }
    }
}
