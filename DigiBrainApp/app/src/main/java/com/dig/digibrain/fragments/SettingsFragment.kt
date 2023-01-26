package com.dig.digibrain.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.dig.digibrain.R
import com.dig.digibrain.activities.SettingsActivity
import com.dig.digibrain.databinding.DialogLanguagesBinding
import com.dig.digibrain.dialogs.ChooseLanguageDialog
import com.dig.digibrain.interfaces.ILanguageChanged
import com.dig.digibrain.models.LanguageModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.SettingsViewModel
import com.dig.digibrain.viewModels.StatisticsViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), ILanguageChanged {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var sessionManager: SessionManager

    private var languages: List<LanguageModel>? = null
    private var currentLanguageCode: String? = "ro"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        sessionManager = SessionManager(requireContext())
        setupViewModel()

        getLanguages()

        val language = requireActivity().getSharedPreferences("application", Context.MODE_PRIVATE)
            .getString("language", null)
        language?.apply {
            changeLanguage(language)
        }

        preferenceManager.findPreference<Preference>("language")?.apply {
            val languagePref = this
            languagePref.setOnPreferenceClickListener {
                languages?.apply {
                    val dialog = ChooseLanguageDialog(currentLanguageCode, this)
                    dialog.addListener(this@SettingsFragment)
                    dialog.show(parentFragmentManager, "Choose language")
                }
                true
            }
        }

        preferenceManager.findPreference<SwitchPreferenceCompat>("dark_mode")?.apply {
            val darkModePref = this
            darkModePref.setOnPreferenceClickListener {
                if(darkModePref.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                requireActivity().getSharedPreferences("application", Context.MODE_PRIVATE).
                    edit().putBoolean("darkMode", darkModePref.isChecked).apply()
                true
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[SettingsViewModel::class.java]
    }

    private fun getLanguages() {
        viewModel.getLanguages()
            .observe(this) {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            languages = resource.data
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    private fun setLocale(languageCode: String) {
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(languageCode))
        resources.updateConfiguration(configuration, metrics)
        if((requireActivity() as SettingsActivity).isBindingSet()) {
            requireActivity().onConfigurationChanged(configuration)
        }
        onConfigurationChanged(configuration)
    }

    override fun changeLanguage(languageCode: String) {
        preferenceManager.findPreference<Preference>("language")?.apply {
            val languagePref = this
            when(languageCode) {
                "ro" -> languagePref.setIcon(R.drawable.flag_of_romania)
                "en" -> languagePref.setIcon(R.drawable.flag_of_england)
            }
            currentLanguageCode = languageCode
        }
        requireActivity().getSharedPreferences("application", Context.MODE_PRIVATE).
            edit().putString("language", languageCode).apply()
        setLocale(languageCode)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        preferenceManager.findPreference<SwitchPreferenceCompat>("dark_mode")?.apply {
            this.title = resources.getString(R.string.dark_mode)
        }
        preferenceManager.findPreference<SwitchPreferenceCompat>("notifications")?.apply {
            this.title = resources.getString(R.string.notifications)
        }
        preferenceManager.findPreference<SwitchPreferenceCompat>("sound")?.apply {
            this.title = resources.getString(R.string.sound)
        }
        preferenceManager.findPreference<Preference>("language")?.apply {
            this.title = resources.getString(R.string.language)
        }
        preferenceManager.findPreference<Preference>("feedback")?.apply {
            this.title = resources.getString(R.string.learn_more)
            this.summary = resources.getString(R.string.learn_more_description)
        }
    }
}