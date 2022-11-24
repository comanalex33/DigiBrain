package com.dig.digibrain.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dig.digibrain.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        preferenceManager.findPreference<Preference>("language")?.apply {
            val languagePref = this
            languagePref.setOnPreferenceClickListener {
                languagePref.setIcon(R.drawable.flag_of_england)
                true
            }
        }


    }
}